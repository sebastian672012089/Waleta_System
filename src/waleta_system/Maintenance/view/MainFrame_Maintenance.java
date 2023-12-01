package waleta_system.Maintenance.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import waleta_system.Maintenance.controller.AlatDAO;
import waleta_system.Maintenance.controller.JadwalDAO;
import waleta_system.Maintenance.controller.KategoriAlatDAO;
import waleta_system.Maintenance.controller.LantaiDAO;
import waleta_system.Maintenance.controller.PathHelper;
import waleta_system.Maintenance.controller.RuanganDAO;
import waleta_system.Maintenance.model.Alat;
import waleta_system.Maintenance.model.Jadwal;
import waleta_system.Maintenance.model.KategoriAlat;
import waleta_system.Maintenance.model.Lantai;
import waleta_system.Maintenance.model.Ruangan;
import waleta_system.Maintenance.view.alat.DaftarAlatJPanel;
import waleta_system.Maintenance.view.alat.DaftarTemuanJPanel;
import waleta_system.Maintenance.view.jadwal.DaftarJadwalJPanel;
import waleta_system.Maintenance.view.kategoriAlat.TambahUbahKategoriAlatJDialog;
import waleta_system.Maintenance.view.lantai.TambahUbahLantaiJDialog;
import waleta_system.Maintenance.view.ruangan.TambahUbahRuanganJDialog;

/**
 *
 * @author User
 */
public class MainFrame_Maintenance extends javax.swing.JFrame {

    Timer timerTV;
    TimerTask timerTaskTV;
    Timer timerTVBlink;
    TimerTask timerTaskTVBlink;
    Timer timerScrollJadwal;
    TimerTask timerTaskScrollJadwal;
    private boolean isInsertOrEditAlat;
    private int idAlatUpdate;

    public enum KategoriDialogAlat {
        TAMBAH,
        UBAH
    }
    private KategoriDialogAlat kategoriDialogAlat;

    private TableModelListener tableModelListenerLantai;
    private TableModelListener tableModelListenerRuangan;
    private TableModelListener tableModelListenerKategoriAlat;
    private JCheckBox checkBoxPilihSemuaLantai;
    private JCheckBox checkBoxPilihSemuaRuangan;
    private JCheckBox checkBoxPilihSemuaKategoriAlat;
    private ArrayList<Lantai> lantais;
    private ArrayList<Ruangan> ruangans;
    private ArrayList<KategoriAlat> kategoriAlats;
    private ArrayList<DaftarAlatJPanel> daftarAlatJPanels;
    private ArrayList<DaftarTemuanJPanel> daftarTemuanJPanels;
    private ArrayList<DaftarJadwalJPanel> daftarJadwalHariIniJPanels;
    private ArrayList<DaftarJadwalJPanel> daftarJadwalTerlambatJPanels;
    public ArrayList<Alat> alats;
    public ArrayList<Jadwal> jadwalHariInis;
    public ArrayList<Jadwal> jadwalTerlambats;
    public ArrayList<Integer> pageLantai;
    private int totalPageLantai;
    private int currPageLantai;
    private String sortByLantai;
    private String sortTypeLantai;
    public ArrayList<Integer> pageRuangan;
    private int totalPageRuangan;
    private int currPageRuangan;
    private String sortByRuangan;
    private String sortTypeRuangan;
    public ArrayList<Integer> pageKategoriAlat;
    private int totalPageKategoriAlat;
    private int currPageKategoriAlat;
    private String sortByKategoriAlat;
    private String sortTypeKategoriAlat;

    private Timer timerPosisiKiri;
    private Timer timerPosisiKanan;
    private Timer timerPosisiAtas;
    private Timer timerPosisiBawah;
    private Timer timerRotasiKiri;
    private Timer timerRotasiKanan;
    private boolean isNeedToWaitRefreshingPeta;
    private boolean isRefreshingPeta;
    private boolean isBlinking;
    private boolean isMouseDown;

    private javax.swing.ImageIcon currImageIcon;

    private final java.awt.event.ItemListener jComboBoxHalamanLantaiItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            jComboBoxHalamanLantaiItemStateChanged(e);
        }
    };

    private final java.awt.event.ItemListener jComboBoxHalamanRuanganItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            jComboBoxHalamanRuanganItemStateChanged(e);
        }
    };

    private final java.awt.event.ItemListener jComboBoxHalamanKategoriAlatItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            jComboBoxHalamanKategoriAlatItemStateChanged(e);
        }
    };

    private final java.awt.event.ItemListener jComboBoxUkuranPetaItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            jComboBoxUkuranPetaItemStateChanged(e);
        }
    };

    private final javax.swing.event.ChangeListener jSliderUkuranPetaChangeListener = new ChangeListener() {

        @Override
        public void stateChanged(ChangeEvent e) {
            refreshPeta(currImageIcon, alats);
        }
    };

    /**
     * Creates new form MainJFrame
     */
    public MainFrame_Maintenance() {
        initComponents();
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            if (jCheckBoxModaTV.isSelected()) {
                                jCheckBoxModaTV.setSelected(false);
                                jCheckBoxModaTVActionPerformed(null);
                            }
                        }
                        return false;
                    }
                });

        Timer timerWaktu = new Timer(true);
        timerWaktu.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEEE, dd MMMM yyyy - HH:mm:ss", new Locale("in", "ID"));
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
                jLabelWaktu.setText(simpleDateFormat.format(new Date()));
            }
        }, 0, 1000);
        try {
            setIconImage(ImageIO.read(new File(getClass().getResource("/waleta_system/Images/logo_waleta.jpg").toString().replace("file:/", "").replace("%20", " "))));
        } catch (IOException ex) {
            Logger.getLogger(MainFrame_Maintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
        isNeedToWaitRefreshingPeta = false;
        isRefreshingPeta = false;
        isInsertOrEditAlat = false;
        currImageIcon = null;
        refreshLantai();
        refreshRuangan();
        refreshKategoriAlat();
        refreshAlat();
        jPanelJadwalHariIni.setVisible(false);
        jPanelJadwalTerlambat.setVisible(false);
        jButtonJeda.setVisible(false);
        jPanelDaftarAlat.setVisible(false);

        jScrollPanePeta.setWheelScrollingEnabled(false);
        jScrollPanePeta.addMouseWheelListener(new MouseAdapter() {
            public void mouseWheelMoved(MouseWheelEvent evt) {
                if (evt.getWheelRotation() == 1)//mouse wheel was rotated down/ towards the user
                {
                    jButtonKurangUkuranPetaActionPerformed(null);
                } else if (evt.getWheelRotation() == -1)//mouse wheel was rotated up/away from the user
                {
                    jButtonTambahUkuranPetaActionPerformed(null);
                }
            }
        });

        jTextFieldCariAlat.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    refreshAlat();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    refreshAlat();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    refreshAlat();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jTextFieldCariLantai.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    refreshJTableLantai();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    refreshJTableLantai();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    refreshJTableLantai();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jTextFieldCariRuangan.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    refreshJTableRuangan();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    refreshJTableRuangan();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    refreshJTableRuangan();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jTextFieldCariKategoriAlat.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    refreshJTableKategoriAlat();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    refreshJTableKategoriAlat();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    refreshJTableKategoriAlat();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jTableLantai.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        checkBoxPilihSemuaLantai = new JCheckBox("");
        checkBoxPilihSemuaLantai.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTableLantai.getModel().removeTableModelListener(tableModelListenerLantai);
                for (int i = 0; i < jTableLantai.getRowCount(); i++) {
                    jTableLantai.setValueAt(checkBoxPilihSemuaLantai.isSelected(), i, 0);
                }
                jTableLantai.getModel().addTableModelListener(tableModelListenerLantai);

                if (checkBoxPilihSemuaLantai.isSelected()) {
                    jLabelTerpilihLantai.setText("Terpilih: " + jTableLantai.getRowCount());
                    jLabelTerpilihLantai.setVisible(true);
                    jButtonHapusTerpilihLantai.setVisible(true);
                } else {
                    jLabelTerpilihLantai.setVisible(false);
                    jButtonHapusTerpilihLantai.setVisible(false);
                }
            }
        });
        jTableLantai.getColumnModel().getColumn(0).setHeaderRenderer(new EditableHeaderRenderer(checkBoxPilihSemuaLantai));
        jTableLantai.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && row > -1) {
                    jTableLantai.setValueAt(!(boolean) jTableLantai.getValueAt(row, 0), row, 0);
                }
            }
        });
        jTableLantai.getTableHeader().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTableLantai.columnAtPoint(e.getPoint());
                if (col != 0 && col != 3) {
                    String name;
                    switch (col) {
                        case 1:
                            name = "Nama Lantai";
                            break;
                        case 2:
                            name = "Gambar";
                            break;
                        default:
                            name = "";
                    }

                    JTableHeader th = jTableLantai.getTableHeader();
                    TableColumnModel tableColumnModel = th.getColumnModel();
                    tableColumnModel.getColumn(col).setHeaderValue(name + " ↑");
                    th.repaint();
                    System.out.println("column index selected " + col + " " + name);
                }

            }
        });

        jTableRuangan.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        checkBoxPilihSemuaRuangan = new JCheckBox("");
        checkBoxPilihSemuaRuangan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTableRuangan.getModel().removeTableModelListener(tableModelListenerRuangan);
                for (int i = 0; i < jTableRuangan.getRowCount(); i++) {
                    jTableRuangan.setValueAt(checkBoxPilihSemuaRuangan.isSelected(), i, 0);
                }
                jTableRuangan.getModel().addTableModelListener(tableModelListenerRuangan);

                if (checkBoxPilihSemuaRuangan.isSelected()) {
                    jLabelTerpilihRuangan.setText("Terpilih: " + jTableRuangan.getRowCount());
                    jLabelTerpilihRuangan.setVisible(true);
                    jButtonHapusTerpilihRuangan.setVisible(true);
                } else {
                    jLabelTerpilihRuangan.setVisible(false);
                    jButtonHapusTerpilihRuangan.setVisible(false);
                }
            }
        });
        jTableRuangan.getColumnModel().getColumn(0).setHeaderRenderer(new EditableHeaderRenderer(checkBoxPilihSemuaRuangan));
        jTableRuangan.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && row > -1) {
                    jTableRuangan.setValueAt(!(boolean) jTableRuangan.getValueAt(row, 0), row, 0);
                }
            }
        });
        jTableRuangan.getTableHeader().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTableRuangan.columnAtPoint(e.getPoint());
                if (col != 0 && col != 3) {
                    String name;
                    switch (col) {
                        case 1:
                            name = "Nama Ruangan";
                            break;
                        default:
                            name = "";
                    }

                    JTableHeader th = jTableRuangan.getTableHeader();
                    TableColumnModel tableColumnModel = th.getColumnModel();
                    tableColumnModel.getColumn(col).setHeaderValue(name + " ↑");
                    th.repaint();
                    System.out.println("column index selected " + col + " " + name);
                }

            }
        });

        jTableKategoriAlat.getTableHeader().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        checkBoxPilihSemuaKategoriAlat = new JCheckBox("");
        checkBoxPilihSemuaKategoriAlat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTableKategoriAlat.getModel().removeTableModelListener(tableModelListenerKategoriAlat);
                for (int i = 0; i < jTableKategoriAlat.getRowCount(); i++) {
                    jTableKategoriAlat.setValueAt(checkBoxPilihSemuaKategoriAlat.isSelected(), i, 0);
                }
                jTableKategoriAlat.getModel().addTableModelListener(tableModelListenerKategoriAlat);

                if (checkBoxPilihSemuaKategoriAlat.isSelected()) {
                    jLabelTerpilihKategoriAlat.setText("Terpilih: " + jTableKategoriAlat.getRowCount());
                    jLabelTerpilihKategoriAlat.setVisible(true);
                    jButtonHapusTerpilihKategoriAlat.setVisible(true);
                } else {
                    jLabelTerpilihKategoriAlat.setVisible(false);
                    jButtonHapusTerpilihKategoriAlat.setVisible(false);
                }
            }
        });
        jTableKategoriAlat.getColumnModel().getColumn(0).setHeaderRenderer(new EditableHeaderRenderer(checkBoxPilihSemuaKategoriAlat));
        jTableKategoriAlat.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table = (JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && row > -1) {
                    jTableKategoriAlat.setValueAt(!(boolean) jTableKategoriAlat.getValueAt(row, 0), row, 0);
                }
            }
        });
        jTableKategoriAlat.getTableHeader().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int col = jTableKategoriAlat.columnAtPoint(e.getPoint());
                if (col != 0 && col != 5) {
                    String name;
                    switch (col) {
                        case 1:
                            name = "Nama Alat";
                            break;
                        case 2:
                            name = "Icon Healthy";
                            break;
                        case 3:
                            name = "Icon In Repair";
                            break;
                        case 4:
                            name = "Icon Damaged";
                            break;
                        default:
                            name = "";
                    }

                    JTableHeader th = jTableKategoriAlat.getTableHeader();
                    TableColumnModel tableColumnModel = th.getColumnModel();
                    tableColumnModel.getColumn(col).setHeaderValue(name + " ↑");
                    th.repaint();
                    System.out.println("column index selected " + col + " " + name);
                }

            }
        });
    }

    class EditableHeaderRenderer implements TableCellRenderer {

        private JTable table = null;
        private MouseEventReposter reporter = null;
        private JCheckBox editor;

        EditableHeaderRenderer(JCheckBox editor) {
            this.editor = editor;
            try {
                this.editor.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            } catch (Exception ex) {
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            if (table != null && this.table != table) {
                this.table = table;
                final JTableHeader header = table.getTableHeader();
                if (header != null) {
                    this.editor.setForeground(header.getForeground());
                    this.editor.setBackground(header.getBackground());
                    this.editor.setFont(header.getFont());
                    Rectangle r = new Rectangle(header.getHeaderRect(0).x, header.getHeaderRect(0).y, header.getHeaderRect(0).width, 22);
                    editor.setBounds(r);
                    header.add(this.editor);
                    reporter = new MouseEventReposter(header, col, this.editor);
                    header.addMouseListener(reporter);
                }
            }

            if (reporter != null) {
                reporter.setColumn(col);
            }

            return this.editor;
        }

        public class MouseEventReposter extends MouseAdapter {

            private Component dispatchComponent;
            private JTableHeader header;
            private int column = -1;
            private Component editor;

            public MouseEventReposter(JTableHeader header, int column, Component editor) {
                this.header = header;
                this.column = column;
                this.editor = editor;
            }

            public void setColumn(int column) {
                this.column = column;
            }

            private void setDispatchComponent(MouseEvent e) {
                int col = header.getTable().columnAtPoint(e.getPoint());
                if (col != column || col == -1) {
                    return;
                }

                Point p = e.getPoint();
                Point p2 = SwingUtilities.convertPoint(header, p, editor);
                dispatchComponent = SwingUtilities.getDeepestComponentAt(editor, p2.x, p2.y);
            }

            private boolean repostEvent(MouseEvent e) {
                if (dispatchComponent == null) {
                    return false;
                }
                MouseEvent e2 = SwingUtilities.convertMouseEvent(header, e, dispatchComponent);
                dispatchComponent.dispatchEvent(e2);
                return true;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (header.getResizingColumn() == null) {
                    Point p = e.getPoint();

                    int col = header.getTable().columnAtPoint(p);
                    if (col != column || col == -1) {
                        return;
                    }

                    int index = header.getColumnModel().getColumnIndexAtX(p.x);
                    if (index == -1) {
                        return;
                    }

                    editor.setBounds(header.getHeaderRect(index));
                    editor.validate();
                    setDispatchComponent(e);
                    repostEvent(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                repostEvent(e);
                dispatchComponent = null;
            }
        }
    }

    public class JPanelAksiLantai extends JPanel {

        private final JButton jButtonUbah;
        private final JButton jButtonHapus;
        private int row;
        private String state;

        public JPanelAksiLantai(final int row) {
            this.row = row;
            setLayout(new GridBagLayout());
            jButtonUbah = new JButton("Ubah");
            jButtonUbah.setActionCommand("ubah");
            jButtonHapus = new JButton("Hapus");
            jButtonHapus.setActionCommand("hapus");

            add(jButtonUbah);
            add(jButtonHapus);

            ActionListener listener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    state = e.getActionCommand();
                    switch (state) {
                        case "ubah":
                            new TambahUbahLantaiJDialog(MainFrame_Maintenance.this, true, MainFrame_Maintenance.this, TambahUbahLantaiJDialog.KategoriDialog.UBAH, lantais.get(row)).setVisible(true);
                            break;
                        case "hapus":
                            String namaLantai = lantais.get(row).getNamaLantai();
                            int opsi = JOptionPane.showConfirmDialog(MainFrame_Maintenance.this, "Apakah anda yakin akan menghapus lantai \"" + namaLantai + "\"?", "Menghapus Data Lantai", JOptionPane.YES_NO_OPTION);
                            if (opsi == JOptionPane.YES_OPTION) {
                                try {
                                    if (new LantaiDAO().delete(MainFrame_Maintenance.this, lantais.get(row).getIdLantai())) {
                                        JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Sukses menghapus lantai \"" + namaLantai + "\"");
                                        refreshLantai();
                                    } else {
                                        JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus lantai");
                                    }
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus lantai: " + ex.getLocalizedMessage() + "");
                                }

                            }
                            break;
                        default:
                            throw new AssertionError();
                    }
                }
            };

            jButtonUbah.addActionListener(listener);
            jButtonHapus.addActionListener(listener);
        }

        public void addActionListener(ActionListener listener) {
            jButtonUbah.addActionListener(listener);
            jButtonHapus.addActionListener(listener);
        }

        public String getState() {
            return state;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }
    }

    public class DefaultTableCellRendererAksiLantai extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanelAksiLantai jPanelAksi = new JPanelAksiLantai(row);
            if (isSelected) {
                jPanelAksi.setForeground(table.getSelectionForeground());
                jPanelAksi.setBackground(table.getSelectionBackground());
            } else {
                jPanelAksi.setForeground(table.getForeground());
                if (row % 2 == 0) {
                    jPanelAksi.setBackground(Color.white);
                } else {
                    jPanelAksi.setBackground(table.getBackground());
                }
            }
            return jPanelAksi;
        }
    }

    public class AbstractCellEditorAksiLantai extends AbstractCellEditor implements TableCellEditor {

        private JPanelAksiLantai jPanelAksiLantai;

        @Override
        public Object getCellEditorValue() {
            return jPanelAksiLantai.getState();
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            jPanelAksiLantai = new JPanelAksiLantai(row);
            jPanelAksiLantai.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            stopCellEditing();
                        }
                    });
                }
            });
            if (isSelected) {
                jPanelAksiLantai.setForeground(table.getSelectionForeground());
                jPanelAksiLantai.setBackground(table.getSelectionBackground());
            } else {
                jPanelAksiLantai.setForeground(table.getForeground());
                jPanelAksiLantai.setBackground(table.getBackground());
            }
            return jPanelAksiLantai;
        }
    }

    public final void refreshLantai() {
        try {
            refreshJTableLantai();
            refreshJComboBoxLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshJComboBoxLantai() throws Exception {
        try {
            lantais = new LantaiDAO().selectAll();
            String[] dataLantaiString = new String[lantais.size() + 1];
            dataLantaiString[0] = "-";
            for (int i = 0; i < lantais.size(); i++) {
                dataLantaiString[i + 1] = lantais.get(i).getNamaLantai();
            }
            jComboBoxLantai.setModel(new javax.swing.DefaultComboBoxModel<>(dataLantaiString));
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void refreshJTableLantai() throws Exception {
        try {
            int total = new LantaiDAO().countLike(jTextFieldCariLantai.getText().toString());
            jComboBoxHalamanLantai.removeItemListener(jComboBoxHalamanLantaiItemListener);
            jComboBoxHalamanLantai.removeAllItems();
            if (total == 0) {
                totalPageLantai = 0;
                currPageLantai = 0;
                jSeparatorAturHalamanLantai.setVisible(false);
                jButtonPertamaLantai.setVisible(false);
                jButtonSebelumnyaLantai.setVisible(false);
                jToggleButtonHal1Lantai.setVisible(false);
                jToggleButtonHal2Lantai.setVisible(false);
                jToggleButtonHal3Lantai.setVisible(false);
                jToggleButtonHal4Lantai.setVisible(false);
                jToggleButtonHal5Lantai.setVisible(false);
                jToggleButtonHal6Lantai.setVisible(false);
                jToggleButtonHal7Lantai.setVisible(false);
                jToggleButtonHal8Lantai.setVisible(false);
                jToggleButtonHal9Lantai.setVisible(false);
                jToggleButtonHal10Lantai.setVisible(false);
                jToggleButtonHal11Lantai.setVisible(false);
                jButtonSelanjutnyaLantai.setVisible(false);
                jButtonTerakhirLantai.setVisible(false);
                jSeparatorPerHalamanLantai.setVisible(false);
                jLabelPerHalamanLantai.setVisible(false);
                jComboBoxPerHalamanLantai.setVisible(false);
                jSeparatorHalamanLantai.setVisible(false);
                jLabelHalamanLantai.setVisible(false);
                jComboBoxHalamanLantai.setVisible(false);
            } else {
                if (total % Integer.parseInt((String) jComboBoxPerHalamanLantai.getSelectedItem()) == 0) {
                    totalPageLantai = total / Integer.parseInt((String) jComboBoxPerHalamanLantai.getSelectedItem());
                } else {
                    totalPageLantai = (total / Integer.parseInt((String) jComboBoxPerHalamanLantai.getSelectedItem())) + 1;
                }
                for (int i = 0; i < totalPageLantai; i++) {
                    jComboBoxHalamanLantai.addItem((i + 1) + "");
                }
                if (currPageLantai < 1) {
                    currPageLantai = 1;
                } else if (currPageLantai > totalPageLantai) {
                    currPageLantai = totalPageLantai;
                }
                for (int i = 0; i < jComboBoxHalamanLantai.getItemCount(); i++) {
                    if (jComboBoxHalamanLantai.getItemAt(i).equals(Integer.toString(currPageLantai))) {
                        jComboBoxHalamanLantai.setSelectedIndex(i);
                        break;
                    }
                }
                jSeparatorPerHalamanLantai.setVisible(true);
                jLabelPerHalamanLantai.setVisible(true);
                jComboBoxPerHalamanLantai.setVisible(true);
                jSeparatorHalamanLantai.setVisible(true);
                jLabelHalamanLantai.setVisible(true);
                jComboBoxHalamanLantai.setVisible(true);
                jButtonPertamaLantai.setVisible(true);
                jButtonSebelumnyaLantai.setVisible(true);
                jButtonTerakhirLantai.setVisible(true);
                jButtonSelanjutnyaLantai.setVisible(true);
                if (currPageLantai == 1) {
                    jButtonPertamaLantai.setEnabled(false);
                    jButtonSebelumnyaLantai.setEnabled(false);
                } else {
                    jButtonPertamaLantai.setEnabled(true);
                    jButtonSebelumnyaLantai.setEnabled(true);
                }
                if (currPageLantai == totalPageLantai) {
                    jButtonTerakhirLantai.setEnabled(false);
                    jButtonSelanjutnyaLantai.setEnabled(false);
                } else {
                    jButtonTerakhirLantai.setEnabled(true);
                    jButtonSelanjutnyaLantai.setEnabled(true);
                }
                int minPage = Math.max(1, currPageLantai - 5);
                int maxPage = Math.min(totalPageLantai, currPageLantai + 5);
                pageLantai = new ArrayList<>();
                for (int i = minPage; i <= maxPage; i++) {
                    pageLantai.add(i);
                }
                if (pageLantai.size() > 0) {
                    jToggleButtonHal1Lantai.setText(pageLantai.get(0).toString());
                    if (pageLantai.get(0) == currPageLantai) {
                        jToggleButtonHal1Lantai.setSelected(true);
                    } else {
                        jToggleButtonHal1Lantai.setSelected(false);
                    }
                    jToggleButtonHal1Lantai.setVisible(true);
                } else {
                    jToggleButtonHal1Lantai.setVisible(false);
                }
                if (pageLantai.size() > 1) {
                    jToggleButtonHal2Lantai.setText(pageLantai.get(1).toString());
                    if (pageLantai.get(1) == currPageLantai) {
                        jToggleButtonHal2Lantai.setSelected(true);
                    } else {
                        jToggleButtonHal2Lantai.setSelected(false);
                    }
                    jToggleButtonHal2Lantai.setVisible(true);
                } else {
                    jToggleButtonHal2Lantai.setVisible(false);
                }
                if (pageLantai.size() > 2) {
                    jToggleButtonHal3Lantai.setText(pageLantai.get(2).toString());
                    if (pageLantai.get(2) == currPageLantai) {
                        jToggleButtonHal3Lantai.setSelected(true);
                    } else {
                        jToggleButtonHal3Lantai.setSelected(false);
                    }
                    jToggleButtonHal3Lantai.setVisible(true);
                } else {
                    jToggleButtonHal3Lantai.setVisible(false);
                }
                if (pageLantai.size() > 3) {
                    jToggleButtonHal4Lantai.setText(pageLantai.get(3).toString());
                    if (pageLantai.get(3) == currPageLantai) {
                        jToggleButtonHal4Lantai.setSelected(true);
                    } else {
                        jToggleButtonHal4Lantai.setSelected(false);
                    }
                    jToggleButtonHal4Lantai.setVisible(true);
                } else {
                    jToggleButtonHal4Lantai.setVisible(false);
                }
                if (pageLantai.size() > 4) {
                    jToggleButtonHal5Lantai.setText(pageLantai.get(4).toString());
                    if (pageLantai.get(4) == currPageLantai) {
                        jToggleButtonHal5Lantai.setSelected(true);
                    } else {
                        jToggleButtonHal5Lantai.setSelected(false);
                    }
                    jToggleButtonHal5Lantai.setVisible(true);
                } else {
                    jToggleButtonHal5Lantai.setVisible(false);
                }
                if (pageLantai.size() > 5) {
                    jToggleButtonHal6Lantai.setText(pageLantai.get(5).toString());
                    if (pageLantai.get(5) == currPageLantai) {
                        jToggleButtonHal6Lantai.setSelected(true);
                    } else {
                        jToggleButtonHal6Lantai.setSelected(false);
                    }
                    jToggleButtonHal6Lantai.setVisible(true);
                } else {
                    jToggleButtonHal6Lantai.setVisible(false);
                }
                if (pageLantai.size() > 6) {
                    jToggleButtonHal7Lantai.setText(pageLantai.get(6).toString());
                    if (pageLantai.get(6) == currPageLantai) {
                        jToggleButtonHal7Lantai.setSelected(true);
                    } else {
                        jToggleButtonHal7Lantai.setSelected(false);
                    }
                    jToggleButtonHal7Lantai.setVisible(true);
                } else {
                    jToggleButtonHal7Lantai.setVisible(false);
                }
                if (pageLantai.size() > 7) {
                    jToggleButtonHal8Lantai.setText(pageLantai.get(7).toString());
                    if (pageLantai.get(7) == currPageLantai) {
                        jToggleButtonHal8Lantai.setSelected(true);
                    } else {
                        jToggleButtonHal8Lantai.setSelected(false);
                    }
                    jToggleButtonHal8Lantai.setVisible(true);
                } else {
                    jToggleButtonHal8Lantai.setVisible(false);
                }
                if (pageLantai.size() > 8) {
                    jToggleButtonHal9Lantai.setText(pageLantai.get(8).toString());
                    if (pageLantai.get(8) == currPageLantai) {
                        jToggleButtonHal9Lantai.setSelected(true);
                    } else {
                        jToggleButtonHal9Lantai.setSelected(false);
                    }
                    jToggleButtonHal9Lantai.setVisible(true);
                } else {
                    jToggleButtonHal9Lantai.setVisible(false);
                }
                if (pageLantai.size() > 9) {
                    jToggleButtonHal10Lantai.setText(pageLantai.get(9).toString());
                    if (pageLantai.get(9) == currPageLantai) {
                        jToggleButtonHal10Lantai.setSelected(true);
                    } else {
                        jToggleButtonHal10Lantai.setSelected(false);
                    }
                    jToggleButtonHal10Lantai.setVisible(true);
                } else {
                    jToggleButtonHal10Lantai.setVisible(false);
                }
                if (pageLantai.size() > 10) {
                    jToggleButtonHal11Lantai.setText(pageLantai.get(10).toString());
                    if (pageLantai.get(10) == currPageLantai) {
                        jToggleButtonHal11Lantai.setSelected(true);
                    } else {
                        jToggleButtonHal11Lantai.setSelected(false);
                    }
                    jToggleButtonHal11Lantai.setVisible(true);
                } else {
                    jToggleButtonHal11Lantai.setVisible(false);
                }
            }
            jComboBoxHalamanLantai.addItemListener(jComboBoxHalamanLantaiItemListener);
            lantais = new LantaiDAO().selectLikePagingSort(jTextFieldCariLantai.getText().toString(), Integer.parseInt(jComboBoxPerHalamanLantai.getSelectedItem().toString()), Integer.parseInt(jComboBoxPerHalamanLantai.getSelectedItem().toString()) * (Math.max(1, currPageLantai) - 1), null, null);
            String[] judulLantai = new String[]{
                "", "Nama Lantai", "Gambar", "Aksi"
            };
            Object[][] dataLantai = new Object[lantais.size()][judulLantai.length];
            String[] dataLantaiString = new String[lantais.size() + 1];
            dataLantaiString[0] = "-";
            for (int i = 0; i < lantais.size(); i++) {
                dataLantai[i][0] = false;
                dataLantai[i][1] = lantais.get(i).getNamaLantai();
                dataLantai[i][2] = lantais.get(i).getGambar();
                dataLantai[i][3] = null;

                dataLantaiString[i + 1] = lantais.get(i).getNamaLantai();
            }

            jTableLantai.setModel(new javax.swing.table.DefaultTableModel(
                    dataLantai,
                    judulLantai
            ) {
                Class[] types = new Class[]{
                    java.lang.Boolean.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
                };
                boolean[] canEdit = new boolean[]{
                    true, false, false, true
                };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
            jLabelTerpilihLantai.setVisible(false);
            jButtonHapusTerpilihLantai.setVisible(false);
            if (checkBoxPilihSemuaLantai != null) {
                checkBoxPilihSemuaLantai.setSelected(false);
            }
            tableModelListenerLantai = new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    int column = e.getColumn();
                    if (column == 0) {
                        int totalSelected = 0;
                        for (int i = 0; i < jTableLantai.getRowCount(); i++) {
                            if ((boolean) jTableLantai.getValueAt(i, 0)) {
                                totalSelected++;
                            }
                        }
                        if (totalSelected == jTableLantai.getRowCount()) {
                            checkBoxPilihSemuaLantai.setSelected(true);
                        } else {
                            checkBoxPilihSemuaLantai.setSelected(false);
                        }

                        if (totalSelected > 0) {
                            jLabelTerpilihLantai.setText("Terpilih: " + totalSelected);
                            jLabelTerpilihLantai.setVisible(true);
                            jButtonHapusTerpilihLantai.setVisible(true);
                        } else {
                            jLabelTerpilihLantai.setVisible(false);
                            jButtonHapusTerpilihLantai.setVisible(false);
                        }
                    }
                }
            };
            jTableLantai.getModel().addTableModelListener(tableModelListenerLantai);
            DefaultTableCellRendererAksiLantai renderer = new DefaultTableCellRendererAksiLantai();
            jTableLantai.getColumnModel().getColumn(3).setCellRenderer(renderer);
            jTableLantai.getColumnModel().getColumn(3).setCellEditor(new AbstractCellEditorAksiLantai());
            jTableLantai.setRowHeight(renderer.getTableCellRendererComponent(jTableLantai, null, true, true, 0, 0).getPreferredSize().height);

            jScrollPaneLantai.setViewportView(jTableLantai);

            if (jTableLantai.getColumnModel().getColumnCount() > 0) {
                jTableLantai.getColumnModel().getColumn(0).setResizable(false);
                jTableLantai.getColumnModel().getColumn(0).setWidth(22);
                jTableLantai.getColumnModel().getColumn(0).setPreferredWidth(22);
                jTableLantai.getColumnModel().getColumn(0).setMinWidth(22);
                jTableLantai.getColumnModel().getColumn(0).setMaxWidth(22);

                jTableLantai.getColumnModel().getColumn(3).setResizable(false);
                jTableLantai.getColumnModel().getColumn(3).setWidth(130);
                jTableLantai.getColumnModel().getColumn(3).setPreferredWidth(130);
                jTableLantai.getColumnModel().getColumn(3).setMinWidth(130);
                jTableLantai.getColumnModel().getColumn(3).setMaxWidth(130);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public class JPanelAksiRuangan extends JPanel {

        private final JButton jButtonUbah;
        private final JButton jButtonHapus;
        private int row;
        private String state;

        public JPanelAksiRuangan(final int row) {
            this.row = row;
            setLayout(new GridBagLayout());
            jButtonUbah = new JButton("Ubah");
            jButtonUbah.setActionCommand("ubah");
            jButtonHapus = new JButton("Hapus");
            jButtonHapus.setActionCommand("hapus");

            add(jButtonUbah);
            add(jButtonHapus);
            ActionListener listener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    state = e.getActionCommand();
                    switch (state) {
                        case "ubah":
                            new TambahUbahRuanganJDialog(MainFrame_Maintenance.this, true, MainFrame_Maintenance.this, TambahUbahRuanganJDialog.KategoriDialog.UBAH, ruangans.get(row)).setVisible(true);
                            break;
                        case "hapus":
                            String namaRuangan = ruangans.get(row).getNamaRuangan();
                            int opsi = JOptionPane.showConfirmDialog(MainFrame_Maintenance.this, "Apakah anda yakin akan menghapus ruangan \"" + namaRuangan + "\"?", "Menghapus Data Ruangan", JOptionPane.YES_NO_OPTION);
                            if (opsi == JOptionPane.YES_OPTION) {
                                try {
                                    if (new RuanganDAO().delete(MainFrame_Maintenance.this, ruangans.get(row).getIdRuangan())) {
                                        JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Sukses menghapus ruangan \"" + namaRuangan + "\"");
                                        refreshRuangan();
                                    } else {
                                        JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus ruangan");
                                    }
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus ruangan: " + ex.getLocalizedMessage() + "");
                                }

                            }
                            break;
                        default:
                            throw new AssertionError();
                    }
                }
            };

            jButtonUbah.addActionListener(listener);
            jButtonHapus.addActionListener(listener);
        }

        public void addActionListener(ActionListener listener) {
            jButtonUbah.addActionListener(listener);
            jButtonHapus.addActionListener(listener);
        }

        public String getState() {
            return state;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }
    }

    public class DefaultTableCellRendererAksiRuangan extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanelAksiRuangan jPanelAksi = new JPanelAksiRuangan(row);
            if (isSelected) {
                jPanelAksi.setForeground(table.getSelectionForeground());
                jPanelAksi.setBackground(table.getSelectionBackground());
            } else {
                jPanelAksi.setForeground(table.getForeground());
                if (row % 2 == 0) {
                    jPanelAksi.setBackground(Color.white);
                } else {
                    jPanelAksi.setBackground(table.getBackground());
                }
            }
            return jPanelAksi;
        }
    }

    public class AbstractCellEditorAksiRuangan extends AbstractCellEditor implements TableCellEditor {

        private JPanelAksiRuangan jPanelAksiRuangan;

        @Override
        public Object getCellEditorValue() {
            return jPanelAksiRuangan.getState();
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            jPanelAksiRuangan = new JPanelAksiRuangan(row);
            jPanelAksiRuangan.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            stopCellEditing();
                        }
                    });
                }
            });
            if (isSelected) {
                jPanelAksiRuangan.setForeground(table.getSelectionForeground());
                jPanelAksiRuangan.setBackground(table.getSelectionBackground());
            } else {
                jPanelAksiRuangan.setForeground(table.getForeground());
                jPanelAksiRuangan.setBackground(table.getBackground());
            }
            return jPanelAksiRuangan;
        }
    }

    public final void refreshRuangan() {
        try {
            refreshJTableRuangan();
            refreshJComboBoxTambahAlatRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshJComboBoxTambahAlatRuangan() throws Exception {
        try {
            ruangans = new RuanganDAO().selectAll();
            String[] dataRuanganString = new String[ruangans.size()];
            for (int i = 0; i < ruangans.size(); i++) {
                dataRuanganString[i] = ruangans.get(i).getNamaRuangan();
            }
            jComboBoxTambahAlatRuangan.setModel(new javax.swing.DefaultComboBoxModel<>(dataRuanganString));
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void refreshJTableRuangan() throws Exception {
        try {
            int total = new RuanganDAO().countLike(jTextFieldCariRuangan.getText().toString());
            jComboBoxHalamanRuangan.removeItemListener(jComboBoxHalamanRuanganItemListener);
            jComboBoxHalamanRuangan.removeAllItems();
            if (total == 0) {
                totalPageRuangan = 0;
                currPageRuangan = 0;
                jSeparatorAturHalamanRuangan.setVisible(false);
                jButtonPertamaRuangan.setVisible(false);
                jButtonSebelumnyaRuangan.setVisible(false);
                jToggleButtonHal1Ruangan.setVisible(false);
                jToggleButtonHal2Ruangan.setVisible(false);
                jToggleButtonHal3Ruangan.setVisible(false);
                jToggleButtonHal4Ruangan.setVisible(false);
                jToggleButtonHal5Ruangan.setVisible(false);
                jToggleButtonHal6Ruangan.setVisible(false);
                jToggleButtonHal7Ruangan.setVisible(false);
                jToggleButtonHal8Ruangan.setVisible(false);
                jToggleButtonHal9Ruangan.setVisible(false);
                jToggleButtonHal10Ruangan.setVisible(false);
                jToggleButtonHal11Ruangan.setVisible(false);
                jButtonSelanjutnyaRuangan.setVisible(false);
                jButtonTerakhirRuangan.setVisible(false);
                jSeparatorPerHalamanRuangan.setVisible(false);
                jLabelPerHalamanRuangan.setVisible(false);
                jComboBoxPerHalamanRuangan.setVisible(false);
                jSeparatorHalamanRuangan.setVisible(false);
                jLabelHalamanRuangan.setVisible(false);
                jComboBoxHalamanRuangan.setVisible(false);
            } else {
                if (total % Integer.parseInt((String) jComboBoxPerHalamanRuangan.getSelectedItem()) == 0) {
                    totalPageRuangan = total / Integer.parseInt((String) jComboBoxPerHalamanRuangan.getSelectedItem());
                } else {
                    totalPageRuangan = (total / Integer.parseInt((String) jComboBoxPerHalamanRuangan.getSelectedItem())) + 1;
                }
                for (int i = 0; i < totalPageRuangan; i++) {
                    jComboBoxHalamanRuangan.addItem((i + 1) + "");
                }
                if (currPageRuangan < 1) {
                    currPageRuangan = 1;
                } else if (currPageRuangan > totalPageRuangan) {
                    currPageRuangan = totalPageRuangan;
                }
                for (int i = 0; i < jComboBoxHalamanRuangan.getItemCount(); i++) {
                    if (jComboBoxHalamanRuangan.getItemAt(i).equals(Integer.toString(currPageRuangan))) {
                        jComboBoxHalamanRuangan.setSelectedIndex(i);
                        break;
                    }
                }
                jSeparatorPerHalamanRuangan.setVisible(true);
                jLabelPerHalamanRuangan.setVisible(true);
                jComboBoxPerHalamanRuangan.setVisible(true);
                jSeparatorHalamanRuangan.setVisible(true);
                jLabelHalamanRuangan.setVisible(true);
                jComboBoxHalamanRuangan.setVisible(true);
                jButtonPertamaRuangan.setVisible(true);
                jButtonSebelumnyaRuangan.setVisible(true);
                jButtonTerakhirRuangan.setVisible(true);
                jButtonSelanjutnyaRuangan.setVisible(true);
                if (currPageRuangan == 1) {
                    jButtonPertamaRuangan.setEnabled(false);
                    jButtonSebelumnyaRuangan.setEnabled(false);
                } else {
                    jButtonPertamaRuangan.setEnabled(true);
                    jButtonSebelumnyaRuangan.setEnabled(true);
                }
                if (currPageRuangan == totalPageRuangan) {
                    jButtonTerakhirRuangan.setEnabled(false);
                    jButtonSelanjutnyaRuangan.setEnabled(false);
                } else {
                    jButtonTerakhirRuangan.setEnabled(true);
                    jButtonSelanjutnyaRuangan.setEnabled(true);
                }
                int minPage = Math.max(1, currPageRuangan - 5);
                int maxPage = Math.min(totalPageRuangan, currPageRuangan + 5);
                pageRuangan = new ArrayList<>();
                for (int i = minPage; i <= maxPage; i++) {
                    pageRuangan.add(i);
                }
                if (pageRuangan.size() > 0) {
                    jToggleButtonHal1Ruangan.setText(pageRuangan.get(0).toString());
                    if (pageRuangan.get(0) == currPageRuangan) {
                        jToggleButtonHal1Ruangan.setSelected(true);
                    } else {
                        jToggleButtonHal1Ruangan.setSelected(false);
                    }
                    jToggleButtonHal1Ruangan.setVisible(true);
                } else {
                    jToggleButtonHal1Ruangan.setVisible(false);
                }
                if (pageRuangan.size() > 1) {
                    jToggleButtonHal2Ruangan.setText(pageRuangan.get(1).toString());
                    if (pageRuangan.get(1) == currPageRuangan) {
                        jToggleButtonHal2Ruangan.setSelected(true);
                    } else {
                        jToggleButtonHal2Ruangan.setSelected(false);
                    }
                    jToggleButtonHal2Ruangan.setVisible(true);
                } else {
                    jToggleButtonHal2Ruangan.setVisible(false);
                }
                if (pageRuangan.size() > 2) {
                    jToggleButtonHal3Ruangan.setText(pageRuangan.get(2).toString());
                    if (pageRuangan.get(2) == currPageRuangan) {
                        jToggleButtonHal3Ruangan.setSelected(true);
                    } else {
                        jToggleButtonHal3Ruangan.setSelected(false);
                    }
                    jToggleButtonHal3Ruangan.setVisible(true);
                } else {
                    jToggleButtonHal3Ruangan.setVisible(false);
                }
                if (pageRuangan.size() > 3) {
                    jToggleButtonHal4Ruangan.setText(pageRuangan.get(3).toString());
                    if (pageRuangan.get(3) == currPageRuangan) {
                        jToggleButtonHal4Ruangan.setSelected(true);
                    } else {
                        jToggleButtonHal4Ruangan.setSelected(false);
                    }
                    jToggleButtonHal4Ruangan.setVisible(true);
                } else {
                    jToggleButtonHal4Ruangan.setVisible(false);
                }
                if (pageRuangan.size() > 4) {
                    jToggleButtonHal5Ruangan.setText(pageRuangan.get(4).toString());
                    if (pageRuangan.get(4) == currPageRuangan) {
                        jToggleButtonHal5Ruangan.setSelected(true);
                    } else {
                        jToggleButtonHal5Ruangan.setSelected(false);
                    }
                    jToggleButtonHal5Ruangan.setVisible(true);
                } else {
                    jToggleButtonHal5Ruangan.setVisible(false);
                }
                if (pageRuangan.size() > 5) {
                    jToggleButtonHal6Ruangan.setText(pageRuangan.get(5).toString());
                    if (pageRuangan.get(5) == currPageRuangan) {
                        jToggleButtonHal6Ruangan.setSelected(true);
                    } else {
                        jToggleButtonHal6Ruangan.setSelected(false);
                    }
                    jToggleButtonHal6Ruangan.setVisible(true);
                } else {
                    jToggleButtonHal6Ruangan.setVisible(false);
                }
                if (pageRuangan.size() > 6) {
                    jToggleButtonHal7Ruangan.setText(pageRuangan.get(6).toString());
                    if (pageRuangan.get(6) == currPageRuangan) {
                        jToggleButtonHal7Ruangan.setSelected(true);
                    } else {
                        jToggleButtonHal7Ruangan.setSelected(false);
                    }
                    jToggleButtonHal7Ruangan.setVisible(true);
                } else {
                    jToggleButtonHal7Ruangan.setVisible(false);
                }
                if (pageRuangan.size() > 7) {
                    jToggleButtonHal8Ruangan.setText(pageRuangan.get(7).toString());
                    if (pageRuangan.get(7) == currPageRuangan) {
                        jToggleButtonHal8Ruangan.setSelected(true);
                    } else {
                        jToggleButtonHal8Ruangan.setSelected(false);
                    }
                    jToggleButtonHal8Ruangan.setVisible(true);
                } else {
                    jToggleButtonHal8Ruangan.setVisible(false);
                }
                if (pageRuangan.size() > 8) {
                    jToggleButtonHal9Ruangan.setText(pageRuangan.get(8).toString());
                    if (pageRuangan.get(8) == currPageRuangan) {
                        jToggleButtonHal9Ruangan.setSelected(true);
                    } else {
                        jToggleButtonHal9Ruangan.setSelected(false);
                    }
                    jToggleButtonHal9Ruangan.setVisible(true);
                } else {
                    jToggleButtonHal9Ruangan.setVisible(false);
                }
                if (pageRuangan.size() > 9) {
                    jToggleButtonHal10Ruangan.setText(pageRuangan.get(9).toString());
                    if (pageRuangan.get(9) == currPageRuangan) {
                        jToggleButtonHal10Ruangan.setSelected(true);
                    } else {
                        jToggleButtonHal10Ruangan.setSelected(false);
                    }
                    jToggleButtonHal10Ruangan.setVisible(true);
                } else {
                    jToggleButtonHal10Ruangan.setVisible(false);
                }
                if (pageRuangan.size() > 10) {
                    jToggleButtonHal11Ruangan.setText(pageRuangan.get(10).toString());
                    if (pageRuangan.get(10) == currPageRuangan) {
                        jToggleButtonHal11Ruangan.setSelected(true);
                    } else {
                        jToggleButtonHal11Ruangan.setSelected(false);
                    }
                    jToggleButtonHal11Ruangan.setVisible(true);
                } else {
                    jToggleButtonHal11Ruangan.setVisible(false);
                }
            }
            jComboBoxHalamanRuangan.addItemListener(jComboBoxHalamanRuanganItemListener);
            ruangans = new RuanganDAO().selectLikePagingSort(jTextFieldCariRuangan.getText().toString(), Integer.parseInt(jComboBoxPerHalamanRuangan.getSelectedItem().toString()), Integer.parseInt(jComboBoxPerHalamanRuangan.getSelectedItem().toString()) * (Math.max(1, currPageRuangan) - 1), null, null);
            String[] judulRuangan = new String[]{
                "", "Lantai", "Nama Ruangan", "Aksi"
            };
            Object[][] dataRuangan = new Object[ruangans.size()][judulRuangan.length];
            String[] dataRuanganString = new String[ruangans.size()];
            for (int i = 0; i < ruangans.size(); i++) {
                dataRuangan[i][0] = false;
                dataRuangan[i][1] = ruangans.get(i).getLantai().getNamaLantai();
                dataRuangan[i][2] = ruangans.get(i).getNamaRuangan();
                dataRuangan[i][3] = null;

                dataRuanganString[i] = ruangans.get(i).getNamaRuangan();
            }

            jTableRuangan.setModel(new javax.swing.table.DefaultTableModel(
                    dataRuangan,
                    judulRuangan
            ) {
                Class[] types = new Class[]{
                    java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
                };
                boolean[] canEdit = new boolean[]{
                    true, false, false, true
                };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
            jLabelTerpilihRuangan.setVisible(false);
            jButtonHapusTerpilihRuangan.setVisible(false);
            if (checkBoxPilihSemuaRuangan != null) {
                checkBoxPilihSemuaRuangan.setSelected(false);
            }
            tableModelListenerRuangan = new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    int column = e.getColumn();
                    if (column == 0) {
                        int totalSelected = 0;
                        for (int i = 0; i < jTableRuangan.getRowCount(); i++) {
                            if ((boolean) jTableRuangan.getValueAt(i, 0)) {
                                totalSelected++;
                            }
                        }
                        if (totalSelected == jTableRuangan.getRowCount()) {
                            checkBoxPilihSemuaRuangan.setSelected(true);
                        } else {
                            checkBoxPilihSemuaRuangan.setSelected(false);
                        }

                        if (totalSelected > 0) {
                            jLabelTerpilihRuangan.setText("Terpilih: " + totalSelected);
                            jLabelTerpilihRuangan.setVisible(true);
                            jButtonHapusTerpilihRuangan.setVisible(true);
                        } else {
                            jLabelTerpilihRuangan.setVisible(false);
                            jButtonHapusTerpilihRuangan.setVisible(false);
                        }
                    }
                }
            };
            jTableRuangan.getModel().addTableModelListener(tableModelListenerRuangan);
            DefaultTableCellRendererAksiRuangan renderer = new DefaultTableCellRendererAksiRuangan();
            jTableRuangan.getColumnModel().getColumn(3).setCellRenderer(renderer);
            jTableRuangan.getColumnModel().getColumn(3).setCellEditor(new AbstractCellEditorAksiRuangan());
            jTableRuangan.setRowHeight(renderer.getTableCellRendererComponent(jTableRuangan, null, true, true, 0, 0).getPreferredSize().height);

            jScrollPaneRuangan.setViewportView(jTableRuangan);

            if (jTableRuangan.getColumnModel().getColumnCount() > 0) {
                jTableRuangan.getColumnModel().getColumn(0).setResizable(false);
                jTableRuangan.getColumnModel().getColumn(0).setWidth(22);
                jTableRuangan.getColumnModel().getColumn(0).setPreferredWidth(22);
                jTableRuangan.getColumnModel().getColumn(0).setMinWidth(22);
                jTableRuangan.getColumnModel().getColumn(0).setMaxWidth(22);

                jTableRuangan.getColumnModel().getColumn(3).setResizable(false);
                jTableRuangan.getColumnModel().getColumn(3).setWidth(130);
                jTableRuangan.getColumnModel().getColumn(3).setPreferredWidth(130);
                jTableRuangan.getColumnModel().getColumn(3).setMinWidth(130);
                jTableRuangan.getColumnModel().getColumn(3).setMaxWidth(130);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public class JPanelAksiKategoriAlat extends JPanel {

        private final JButton jButtonUbah;
        private final JButton jButtonHapus;
        private int row;
        private String state;

        public JPanelAksiKategoriAlat(final int row) {
            this.row = row;
            setLayout(new GridBagLayout());
            jButtonUbah = new JButton("Ubah");
            jButtonUbah.setActionCommand("ubah");
            jButtonHapus = new JButton("Hapus");
            jButtonHapus.setActionCommand("hapus");

            add(jButtonUbah);
            add(jButtonHapus);
            ActionListener listener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    state = e.getActionCommand();
                    switch (state) {
                        case "ubah":
                            new TambahUbahKategoriAlatJDialog(MainFrame_Maintenance.this, true, MainFrame_Maintenance.this, TambahUbahKategoriAlatJDialog.KategoriDialog.UBAH, kategoriAlats.get(row)).setVisible(true);
                            break;
                        case "hapus":
                            String namaAlat = kategoriAlats.get(row).getNamaAlat();
                            int opsi = JOptionPane.showConfirmDialog(MainFrame_Maintenance.this, "Apakah anda yakin akan menghapus kategori alat \"" + namaAlat + "\"?", "Menghapus Data Kategori Alat", JOptionPane.YES_NO_OPTION);
                            if (opsi == JOptionPane.YES_OPTION) {
                                try {
                                    if (new KategoriAlatDAO().delete(MainFrame_Maintenance.this, kategoriAlats.get(row).getIdKategoriAlat())) {
                                        JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Sukses menghapus kategori alat \"" + namaAlat + "\"");
                                        refreshKategoriAlat();
                                    } else {
                                        JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus kategori alat");
                                    }
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus kategori alat: " + ex.getLocalizedMessage() + "");
                                }
                            }
                            break;
                        default:
                            throw new AssertionError();
                    }
                }
            };

            jButtonUbah.addActionListener(listener);
            jButtonHapus.addActionListener(listener);
        }

        public void addActionListener(ActionListener listener) {
            jButtonUbah.addActionListener(listener);
            jButtonHapus.addActionListener(listener);
        }

        public String getState() {
            return state;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }
    }

    public class DefaultTableCellRendererAksiKategoriAlat extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanelAksiKategoriAlat jPanelAksi = new JPanelAksiKategoriAlat(row);
            if (isSelected) {
                jPanelAksi.setForeground(table.getSelectionForeground());
                jPanelAksi.setBackground(table.getSelectionBackground());
            } else {
                jPanelAksi.setForeground(table.getForeground());
                if (row % 2 == 0) {
                    jPanelAksi.setBackground(Color.white);
                } else {
                    jPanelAksi.setBackground(table.getBackground());
                }
            }
            return jPanelAksi;
        }
    }

    public class AbstractCellEditorAksiKategoriAlat extends AbstractCellEditor implements TableCellEditor {

        private JPanelAksiKategoriAlat jPanelAksiKategoriAlat;

        @Override
        public Object getCellEditorValue() {
            return jPanelAksiKategoriAlat.getState();
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            jPanelAksiKategoriAlat = new JPanelAksiKategoriAlat(row);
            jPanelAksiKategoriAlat.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            stopCellEditing();
                        }
                    });
                }
            });
            if (isSelected) {
                jPanelAksiKategoriAlat.setForeground(table.getSelectionForeground());
                jPanelAksiKategoriAlat.setBackground(table.getSelectionBackground());
            } else {
                jPanelAksiKategoriAlat.setForeground(table.getForeground());
                jPanelAksiKategoriAlat.setBackground(table.getBackground());
            }
            return jPanelAksiKategoriAlat;
        }
    }

    public final void refreshKategoriAlat() {
        try {
            refreshJTableKategoriAlat();
            refreshJComboBoxKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshJComboBoxKategoriAlat() throws Exception {
        try {
            kategoriAlats = new KategoriAlatDAO().selectAll();
            String[] dataKategoriAlatString = new String[kategoriAlats.size()];
            for (int i = 0; i < kategoriAlats.size(); i++) {
                dataKategoriAlatString[i] = kategoriAlats.get(i).getNamaAlat();
            }
            jComboBoxTambahAlatKategoriAlat.setModel(new javax.swing.DefaultComboBoxModel<>(dataKategoriAlatString));
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void refreshJTableKategoriAlat() throws Exception {
        try {
            int total = new KategoriAlatDAO().countLike(jTextFieldCariKategoriAlat.getText().toString());
            jComboBoxHalamanKategoriAlat.removeItemListener(jComboBoxHalamanKategoriAlatItemListener);
            jComboBoxHalamanKategoriAlat.removeAllItems();
            if (total == 0) {
                totalPageKategoriAlat = 0;
                currPageKategoriAlat = 0;
                jSeparatorAturHalamanKategoriAlat.setVisible(false);
                jButtonPertamaKategoriAlat.setVisible(false);
                jButtonSebelumnyaKategoriAlat.setVisible(false);
                jToggleButtonHal1KategoriAlat.setVisible(false);
                jToggleButtonHal2KategoriAlat.setVisible(false);
                jToggleButtonHal3KategoriAlat.setVisible(false);
                jToggleButtonHal4KategoriAlat.setVisible(false);
                jToggleButtonHal5KategoriAlat.setVisible(false);
                jToggleButtonHal6KategoriAlat.setVisible(false);
                jToggleButtonHal7KategoriAlat.setVisible(false);
                jToggleButtonHal8KategoriAlat.setVisible(false);
                jToggleButtonHal9KategoriAlat.setVisible(false);
                jToggleButtonHal10KategoriAlat.setVisible(false);
                jToggleButtonHal11KategoriAlat.setVisible(false);
                jButtonSelanjutnyaKategoriAlat.setVisible(false);
                jButtonTerakhirKategoriAlat.setVisible(false);
                jSeparatorPerHalamanKategoriAlat.setVisible(false);
                jLabelPerHalamanKategoriAlat.setVisible(false);
                jComboBoxPerHalamanKategoriAlat.setVisible(false);
                jSeparatorHalamanKategoriAlat.setVisible(false);
                jLabelHalamanKategoriAlat.setVisible(false);
                jComboBoxHalamanKategoriAlat.setVisible(false);
            } else {
                if (total % Integer.parseInt((String) jComboBoxPerHalamanKategoriAlat.getSelectedItem()) == 0) {
                    totalPageKategoriAlat = total / Integer.parseInt((String) jComboBoxPerHalamanKategoriAlat.getSelectedItem());
                } else {
                    totalPageKategoriAlat = (total / Integer.parseInt((String) jComboBoxPerHalamanKategoriAlat.getSelectedItem())) + 1;
                }
                for (int i = 0; i < totalPageKategoriAlat; i++) {
                    jComboBoxHalamanKategoriAlat.addItem((i + 1) + "");
                }
                if (currPageKategoriAlat < 1) {
                    currPageKategoriAlat = 1;
                } else if (currPageKategoriAlat > totalPageKategoriAlat) {
                    currPageKategoriAlat = totalPageKategoriAlat;
                }
                for (int i = 0; i < jComboBoxHalamanKategoriAlat.getItemCount(); i++) {
                    if (jComboBoxHalamanKategoriAlat.getItemAt(i).equals(Integer.toString(currPageKategoriAlat))) {
                        jComboBoxHalamanKategoriAlat.setSelectedIndex(i);
                        break;
                    }
                }
                jSeparatorPerHalamanKategoriAlat.setVisible(true);
                jLabelPerHalamanKategoriAlat.setVisible(true);
                jComboBoxPerHalamanKategoriAlat.setVisible(true);
                jSeparatorHalamanKategoriAlat.setVisible(true);
                jLabelHalamanKategoriAlat.setVisible(true);
                jComboBoxHalamanKategoriAlat.setVisible(true);
                jButtonPertamaKategoriAlat.setVisible(true);
                jButtonSebelumnyaKategoriAlat.setVisible(true);
                jButtonTerakhirKategoriAlat.setVisible(true);
                jButtonSelanjutnyaKategoriAlat.setVisible(true);
                if (currPageKategoriAlat == 1) {
                    jButtonPertamaKategoriAlat.setEnabled(false);
                    jButtonSebelumnyaKategoriAlat.setEnabled(false);
                } else {
                    jButtonPertamaKategoriAlat.setEnabled(true);
                    jButtonSebelumnyaKategoriAlat.setEnabled(true);
                }
                if (currPageKategoriAlat == totalPageKategoriAlat) {
                    jButtonTerakhirKategoriAlat.setEnabled(false);
                    jButtonSelanjutnyaKategoriAlat.setEnabled(false);
                } else {
                    jButtonTerakhirKategoriAlat.setEnabled(true);
                    jButtonSelanjutnyaKategoriAlat.setEnabled(true);
                }
                int minPage = Math.max(1, currPageKategoriAlat - 5);
                int maxPage = Math.min(totalPageKategoriAlat, currPageKategoriAlat + 5);
                pageKategoriAlat = new ArrayList<>();
                for (int i = minPage; i <= maxPage; i++) {
                    pageKategoriAlat.add(i);
                }
                if (pageKategoriAlat.size() > 0) {
                    jToggleButtonHal1KategoriAlat.setText(pageKategoriAlat.get(0).toString());
                    if (pageKategoriAlat.get(0) == currPageKategoriAlat) {
                        jToggleButtonHal1KategoriAlat.setSelected(true);
                    } else {
                        jToggleButtonHal1KategoriAlat.setSelected(false);
                    }
                    jToggleButtonHal1KategoriAlat.setVisible(true);
                } else {
                    jToggleButtonHal1KategoriAlat.setVisible(false);
                }
                if (pageKategoriAlat.size() > 1) {
                    jToggleButtonHal2KategoriAlat.setText(pageKategoriAlat.get(1).toString());
                    if (pageKategoriAlat.get(1) == currPageKategoriAlat) {
                        jToggleButtonHal2KategoriAlat.setSelected(true);
                    } else {
                        jToggleButtonHal2KategoriAlat.setSelected(false);
                    }
                    jToggleButtonHal2KategoriAlat.setVisible(true);
                } else {
                    jToggleButtonHal2KategoriAlat.setVisible(false);
                }
                if (pageKategoriAlat.size() > 2) {
                    jToggleButtonHal3KategoriAlat.setText(pageKategoriAlat.get(2).toString());
                    if (pageKategoriAlat.get(2) == currPageKategoriAlat) {
                        jToggleButtonHal3KategoriAlat.setSelected(true);
                    } else {
                        jToggleButtonHal3KategoriAlat.setSelected(false);
                    }
                    jToggleButtonHal3KategoriAlat.setVisible(true);
                } else {
                    jToggleButtonHal3KategoriAlat.setVisible(false);
                }
                if (pageKategoriAlat.size() > 3) {
                    jToggleButtonHal4KategoriAlat.setText(pageKategoriAlat.get(3).toString());
                    if (pageKategoriAlat.get(3) == currPageKategoriAlat) {
                        jToggleButtonHal4KategoriAlat.setSelected(true);
                    } else {
                        jToggleButtonHal4KategoriAlat.setSelected(false);
                    }
                    jToggleButtonHal4KategoriAlat.setVisible(true);
                } else {
                    jToggleButtonHal4KategoriAlat.setVisible(false);
                }
                if (pageKategoriAlat.size() > 4) {
                    jToggleButtonHal5KategoriAlat.setText(pageKategoriAlat.get(4).toString());
                    if (pageKategoriAlat.get(4) == currPageKategoriAlat) {
                        jToggleButtonHal5KategoriAlat.setSelected(true);
                    } else {
                        jToggleButtonHal5KategoriAlat.setSelected(false);
                    }
                    jToggleButtonHal5KategoriAlat.setVisible(true);
                } else {
                    jToggleButtonHal5KategoriAlat.setVisible(false);
                }
                if (pageKategoriAlat.size() > 5) {
                    jToggleButtonHal6KategoriAlat.setText(pageKategoriAlat.get(5).toString());
                    if (pageKategoriAlat.get(5) == currPageKategoriAlat) {
                        jToggleButtonHal6KategoriAlat.setSelected(true);
                    } else {
                        jToggleButtonHal6KategoriAlat.setSelected(false);
                    }
                    jToggleButtonHal6KategoriAlat.setVisible(true);
                } else {
                    jToggleButtonHal6KategoriAlat.setVisible(false);
                }
                if (pageKategoriAlat.size() > 6) {
                    jToggleButtonHal7KategoriAlat.setText(pageKategoriAlat.get(6).toString());
                    if (pageKategoriAlat.get(6) == currPageKategoriAlat) {
                        jToggleButtonHal7KategoriAlat.setSelected(true);
                    } else {
                        jToggleButtonHal7KategoriAlat.setSelected(false);
                    }
                    jToggleButtonHal7KategoriAlat.setVisible(true);
                } else {
                    jToggleButtonHal7KategoriAlat.setVisible(false);
                }
                if (pageKategoriAlat.size() > 7) {
                    jToggleButtonHal8KategoriAlat.setText(pageKategoriAlat.get(7).toString());
                    if (pageKategoriAlat.get(7) == currPageKategoriAlat) {
                        jToggleButtonHal8KategoriAlat.setSelected(true);
                    } else {
                        jToggleButtonHal8KategoriAlat.setSelected(false);
                    }
                    jToggleButtonHal8KategoriAlat.setVisible(true);
                } else {
                    jToggleButtonHal8KategoriAlat.setVisible(false);
                }
                if (pageKategoriAlat.size() > 8) {
                    jToggleButtonHal9KategoriAlat.setText(pageKategoriAlat.get(8).toString());
                    if (pageKategoriAlat.get(8) == currPageKategoriAlat) {
                        jToggleButtonHal9KategoriAlat.setSelected(true);
                    } else {
                        jToggleButtonHal9KategoriAlat.setSelected(false);
                    }
                    jToggleButtonHal9KategoriAlat.setVisible(true);
                } else {
                    jToggleButtonHal9KategoriAlat.setVisible(false);
                }
                if (pageKategoriAlat.size() > 9) {
                    jToggleButtonHal10KategoriAlat.setText(pageKategoriAlat.get(9).toString());
                    if (pageKategoriAlat.get(9) == currPageKategoriAlat) {
                        jToggleButtonHal10KategoriAlat.setSelected(true);
                    } else {
                        jToggleButtonHal10KategoriAlat.setSelected(false);
                    }
                    jToggleButtonHal10KategoriAlat.setVisible(true);
                } else {
                    jToggleButtonHal10KategoriAlat.setVisible(false);
                }
                if (pageKategoriAlat.size() > 10) {
                    jToggleButtonHal11KategoriAlat.setText(pageKategoriAlat.get(10).toString());
                    if (pageKategoriAlat.get(10) == currPageKategoriAlat) {
                        jToggleButtonHal11KategoriAlat.setSelected(true);
                    } else {
                        jToggleButtonHal11KategoriAlat.setSelected(false);
                    }
                    jToggleButtonHal11KategoriAlat.setVisible(true);
                } else {
                    jToggleButtonHal11KategoriAlat.setVisible(false);
                }
            }
            jComboBoxHalamanKategoriAlat.addItemListener(jComboBoxHalamanKategoriAlatItemListener);
            kategoriAlats = new KategoriAlatDAO().selectLikePagingSort(jTextFieldCariKategoriAlat.getText().toString(), Integer.parseInt(jComboBoxPerHalamanKategoriAlat.getSelectedItem().toString()), Integer.parseInt(jComboBoxPerHalamanKategoriAlat.getSelectedItem().toString()) * (Math.max(1, currPageKategoriAlat) - 1), null, null);
            String[] judulKategoriAlat = new String[]{
                "", "Nama Alat", "Icon Healthy", "Icon In Repair", "Icon Damaged", "Aksi"
            };
            Object[][] dataKategoriAlat = new Object[kategoriAlats.size()][judulKategoriAlat.length];
            String[] dataKategoriAlatString = new String[kategoriAlats.size() + 1];
            dataKategoriAlatString[0] = "-";
            for (int i = 0; i < kategoriAlats.size(); i++) {
                dataKategoriAlat[i][0] = false;
                dataKategoriAlat[i][1] = kategoriAlats.get(i).getNamaAlat();
                dataKategoriAlat[i][2] = kategoriAlats.get(i).getIconHealthy();
                dataKategoriAlat[i][3] = kategoriAlats.get(i).getIconInRepair();
                dataKategoriAlat[i][4] = kategoriAlats.get(i).getIconDamaged();
                dataKategoriAlat[i][5] = null;

                dataKategoriAlatString[i + 1] = kategoriAlats.get(i).getNamaAlat();
            }

            jTableKategoriAlat.setModel(new javax.swing.table.DefaultTableModel(
                    dataKategoriAlat,
                    judulKategoriAlat
            ) {
                Class[] types = new Class[]{
                    java.lang.Boolean.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
                };
                boolean[] canEdit = new boolean[]{
                    true, false, false, false, false, true
                };

                @Override
                public Class getColumnClass(int columnIndex) {
                    return types[columnIndex];
                }

                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
            jLabelTerpilihKategoriAlat.setVisible(false);
            jButtonHapusTerpilihKategoriAlat.setVisible(false);
            if (checkBoxPilihSemuaKategoriAlat != null) {
                checkBoxPilihSemuaKategoriAlat.setSelected(false);
            }
            tableModelListenerKategoriAlat = new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    int column = e.getColumn();
                    if (column == 0) {
                        int totalSelected = 0;
                        for (int i = 0; i < jTableKategoriAlat.getRowCount(); i++) {
                            if ((boolean) jTableKategoriAlat.getValueAt(i, 0)) {
                                totalSelected++;
                            }
                        }
                        if (totalSelected == jTableKategoriAlat.getRowCount()) {
                            checkBoxPilihSemuaKategoriAlat.setSelected(true);
                        } else {
                            checkBoxPilihSemuaKategoriAlat.setSelected(false);
                        }

                        if (totalSelected > 0) {
                            jLabelTerpilihKategoriAlat.setText("Terpilih: " + totalSelected);
                            jLabelTerpilihKategoriAlat.setVisible(true);
                            jButtonHapusTerpilihKategoriAlat.setVisible(true);
                        } else {
                            jLabelTerpilihKategoriAlat.setVisible(false);
                            jButtonHapusTerpilihKategoriAlat.setVisible(false);
                        }
                    }
                }
            };
            jTableKategoriAlat.getModel().addTableModelListener(tableModelListenerKategoriAlat);
            DefaultTableCellRendererAksiKategoriAlat renderer = new DefaultTableCellRendererAksiKategoriAlat();
            jTableKategoriAlat.getColumnModel().getColumn(5).setCellRenderer(renderer);
            jTableKategoriAlat.getColumnModel().getColumn(5).setCellEditor(new AbstractCellEditorAksiKategoriAlat());
            jTableKategoriAlat.setRowHeight(renderer.getTableCellRendererComponent(jTableKategoriAlat, null, true, true, 0, 0).getPreferredSize().height);

            jScrollPaneKategoriAlat.setViewportView(jTableKategoriAlat);

            if (jTableKategoriAlat.getColumnModel().getColumnCount() > 0) {
                jTableKategoriAlat.getColumnModel().getColumn(0).setResizable(false);
                jTableKategoriAlat.getColumnModel().getColumn(0).setWidth(22);
                jTableKategoriAlat.getColumnModel().getColumn(0).setPreferredWidth(22);
                jTableKategoriAlat.getColumnModel().getColumn(0).setMinWidth(22);
                jTableKategoriAlat.getColumnModel().getColumn(0).setMaxWidth(22);

                jTableKategoriAlat.getColumnModel().getColumn(5).setResizable(false);
                jTableKategoriAlat.getColumnModel().getColumn(5).setWidth(130);
                jTableKategoriAlat.getColumnModel().getColumn(5).setPreferredWidth(130);
                jTableKategoriAlat.getColumnModel().getColumn(5).setMinWidth(130);
                jTableKategoriAlat.getColumnModel().getColumn(5).setMaxWidth(130);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void refreshAlat() {
        try {
            refreshJPanelListDaftarAlat();
            refreshPeta(currImageIcon, alats);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshJPanelListDaftarAlat() throws Exception {
        jCheckBoxPilihSemuaAlat.setText("Pilih Semua");
        jCheckBoxPilihSemuaAlat.setSelected(false);
        jToggleButtonPerbesarSemuaAlat.setText("🗖");
        jToggleButtonPerbesarSemuaAlat.setSelected(true);
        jLabelTerpilihAlat.setVisible(false);
        jButtonHapusTerpilihAlat.setVisible(false);
        if (jComboBoxLantai.getSelectedIndex() > 0) {
            if (jCheckBoxModaTV.isSelected()) {
                alats = new AlatDAO().selectTemuanWhereIdLantaiLike(lantais.get(jComboBoxLantai.getSelectedIndex() - 1).getIdLantai(), jTextFieldCariAlat.getText().toString());
            } else {
                alats = new AlatDAO().selectWhereIdLantaiLike(lantais.get(jComboBoxLantai.getSelectedIndex() - 1).getIdLantai(), jTextFieldCariAlat.getText().toString());
            }
        } else {
            if (jCheckBoxModaTV.isSelected()) {
                alats = new AlatDAO().selectTemuanWhereIdLantaiLike(lantais.get(0).getIdLantai(), jTextFieldCariAlat.getText().toString());
            } else {
                alats = new ArrayList<>();
            }
        }
        try {
            if (jCheckBoxModaTV.isSelected()) {
                jPanelDaftarAlat.setBorder(javax.swing.BorderFactory.createTitledBorder("Daftar Temuan"));
                daftarTemuanJPanels = new ArrayList<>();
                for (int i = 0; i < alats.size(); i++) {
                    DaftarTemuanJPanel daftarTemuanJPanel = new DaftarTemuanJPanel(MainFrame_Maintenance.this, i);
                    daftarTemuanJPanels.add(daftarTemuanJPanel);
                }
                jPanelListDaftarAlat = new javax.swing.JPanel();
                jPanelListDaftarAlat.setBackground(Color.WHITE);

                javax.swing.GroupLayout jPanelListDaftarAlatLayout = new javax.swing.GroupLayout(jPanelListDaftarAlat);
                ParallelGroup parallelGroup = jPanelListDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
                for (int i = 0; i < daftarTemuanJPanels.size(); i++) {
                    parallelGroup.addComponent(daftarTemuanJPanels.get(i), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
                }

                jPanelListDaftarAlat.setLayout(jPanelListDaftarAlatLayout);
                jPanelListDaftarAlatLayout.setHorizontalGroup(
                        jPanelListDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelListDaftarAlatLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(parallelGroup)
                                        .addContainerGap(89, Short.MAX_VALUE))
                );

                SequentialGroup sequentialGroup = jPanelListDaftarAlatLayout.createSequentialGroup().addContainerGap();
                for (int i = 0; i < daftarTemuanJPanels.size(); i++) {
                    sequentialGroup.addComponent(daftarTemuanJPanels.get(i), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
                }
                sequentialGroup.addContainerGap(10, Short.MAX_VALUE);
                jPanelListDaftarAlatLayout.setVerticalGroup(
                        jPanelListDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(sequentialGroup)
                );

                jScrollPane1.setViewportView(jPanelListDaftarAlat);
            } else {
                jPanelDaftarAlat.setBorder(javax.swing.BorderFactory.createTitledBorder("Daftar Alat"));
                daftarAlatJPanels = new ArrayList<>();
                for (int i = 0; i < alats.size(); i++) {
                    DaftarAlatJPanel daftarAlatJPanel = new DaftarAlatJPanel(MainFrame_Maintenance.this, i);
                    daftarAlatJPanels.add(daftarAlatJPanel);
                }
                jPanelListDaftarAlat = new javax.swing.JPanel();
                jPanelListDaftarAlat.setBackground(Color.WHITE);

                javax.swing.GroupLayout jPanelListDaftarAlatLayout = new javax.swing.GroupLayout(jPanelListDaftarAlat);
                ParallelGroup parallelGroup = jPanelListDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
                for (int i = 0; i < daftarAlatJPanels.size(); i++) {
                    parallelGroup.addComponent(daftarAlatJPanels.get(i), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
                }

                jPanelListDaftarAlat.setLayout(jPanelListDaftarAlatLayout);
                jPanelListDaftarAlatLayout.setHorizontalGroup(
                        jPanelListDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelListDaftarAlatLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(parallelGroup)
                                        .addContainerGap(89, Short.MAX_VALUE))
                );

                SequentialGroup sequentialGroup = jPanelListDaftarAlatLayout.createSequentialGroup().addContainerGap();
                for (int i = 0; i < daftarAlatJPanels.size(); i++) {
                    sequentialGroup.addComponent(daftarAlatJPanels.get(i), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
                }
                sequentialGroup.addContainerGap(10, Short.MAX_VALUE);
                jPanelListDaftarAlatLayout.setVerticalGroup(
                        jPanelListDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(sequentialGroup)
                );

                jScrollPane1.setViewportView(jPanelListDaftarAlat);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void refreshJadwalHariIni() {
        try {
            refreshJPanelListDaftarJadwalHariIni();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Jadwal Hari Ini: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshJPanelListDaftarJadwalHariIni() throws Exception {
        jadwalHariInis = new JadwalDAO().selectHariIniWhereIdLantai();
        try {
            if (jadwalHariInis.size() > 0) {
                jPanelJadwalHariIni.setVisible(true);
                daftarJadwalHariIniJPanels = new ArrayList<>();
                for (int i = 0; i < jadwalHariInis.size(); i++) {
                    DaftarJadwalJPanel daftarJadwalJPanel = new DaftarJadwalJPanel(MainFrame_Maintenance.this, i, true);
                    daftarJadwalHariIniJPanels.add(daftarJadwalJPanel);
                }
                jPanelListDaftarJadwalHariIni = new javax.swing.JPanel();
                jPanelListDaftarJadwalHariIni.setBackground(Color.WHITE);

                javax.swing.GroupLayout jPanelListDaftarJadwalHariIniLayout = new javax.swing.GroupLayout(jPanelListDaftarJadwalHariIni);
                ParallelGroup parallelGroup = jPanelListDaftarJadwalHariIniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
                for (int i = 0; i < daftarJadwalHariIniJPanels.size(); i++) {
                    parallelGroup.addComponent(daftarJadwalHariIniJPanels.get(i), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
                }

                jPanelListDaftarJadwalHariIni.setLayout(jPanelListDaftarJadwalHariIniLayout);
                jPanelListDaftarJadwalHariIniLayout.setVerticalGroup(
                        jPanelListDaftarJadwalHariIniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelListDaftarJadwalHariIniLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(parallelGroup)
                                        .addContainerGap(89, Short.MAX_VALUE))
                );

                SequentialGroup sequentialGroup = jPanelListDaftarJadwalHariIniLayout.createSequentialGroup().addContainerGap();
                for (int i = 0; i < daftarJadwalHariIniJPanels.size(); i++) {
                    sequentialGroup.addComponent(daftarJadwalHariIniJPanels.get(i), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
                }
                sequentialGroup.addContainerGap(10, Short.MAX_VALUE);
                jPanelListDaftarJadwalHariIniLayout.setHorizontalGroup(
                        jPanelListDaftarJadwalHariIniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(sequentialGroup)
                );

                jScrollPane4.setViewportView(jPanelListDaftarJadwalHariIni);
            } else {
                jPanelJadwalHariIni.setVisible(false);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void refreshJadwalTerlambat() {
        try {
            refreshJPanelListDaftarJadwalTerlambat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Jadwal Terlambat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshJPanelListDaftarJadwalTerlambat() throws Exception {
        jadwalTerlambats = new JadwalDAO().selectTerlambatWhereIdLantai();
        try {
            if (jadwalTerlambats.size() > 0) {
                jPanelJadwalTerlambat.setVisible(true);
                daftarJadwalTerlambatJPanels = new ArrayList<>();
                for (int i = 0; i < jadwalTerlambats.size(); i++) {
                    DaftarJadwalJPanel daftarJadwalJPanel = new DaftarJadwalJPanel(MainFrame_Maintenance.this, i, false);
                    daftarJadwalTerlambatJPanels.add(daftarJadwalJPanel);
                }
                jPanelListDaftarJadwalTerlambat = new javax.swing.JPanel();
                jPanelListDaftarJadwalTerlambat.setBackground(Color.WHITE);

                javax.swing.GroupLayout jPanelListDaftarJadwalTerlambatLayout = new javax.swing.GroupLayout(jPanelListDaftarJadwalTerlambat);
                ParallelGroup parallelGroup = jPanelListDaftarJadwalTerlambatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
                for (int i = 0; i < daftarJadwalTerlambatJPanels.size(); i++) {
                    parallelGroup.addComponent(daftarJadwalTerlambatJPanels.get(i), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
                }

                jPanelListDaftarJadwalTerlambat.setLayout(jPanelListDaftarJadwalTerlambatLayout);
                jPanelListDaftarJadwalTerlambatLayout.setVerticalGroup(
                        jPanelListDaftarJadwalTerlambatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanelListDaftarJadwalTerlambatLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(parallelGroup)
                                        .addContainerGap(89, Short.MAX_VALUE))
                );

                SequentialGroup sequentialGroup = jPanelListDaftarJadwalTerlambatLayout.createSequentialGroup().addContainerGap();
                for (int i = 0; i < daftarJadwalTerlambatJPanels.size(); i++) {
                    sequentialGroup.addComponent(daftarJadwalTerlambatJPanels.get(i), javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
                }
                sequentialGroup.addContainerGap(10, Short.MAX_VALUE);
                jPanelListDaftarJadwalTerlambatLayout.setHorizontalGroup(
                        jPanelListDaftarJadwalTerlambatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(sequentialGroup)
                );

                jScrollPane5.setViewportView(jPanelListDaftarJadwalTerlambat);
            } else {
                jPanelJadwalTerlambat.setVisible(false);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void refreshPeta(javax.swing.ImageIcon currImageIcon2, ArrayList<Alat> currAlats) {
        if (!(isNeedToWaitRefreshingPeta && isRefreshingPeta)) {
            isRefreshingPeta = true;
            if (currImageIcon2 != null) {
                if (jSliderUkuranPeta.getValue() != 0) {
                    try {
                        ImageIcon myImage = currImageIcon2;
                        Image img = myImage.getImage();

                        BufferedImage newImg = new BufferedImage(currImageIcon2.getIconWidth(), currImageIcon2.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

                        Graphics2D g = (Graphics2D) newImg.getGraphics();
                        g.fillRect(0, 0, currImageIcon2.getIconWidth(), currImageIcon2.getIconHeight());
                        g.drawImage(img, 0, 0, null);
                        if (isInsertOrEditAlat) {
                            if (jComboBoxTambahAlatKategoriAlat.getSelectedIndex() >= 0) {
                                BufferedImage additionalImage = ImageIO.read(new File((PathHelper.path + "kategori_alat/icon_healthy/" + kategoriAlats.get(jComboBoxTambahAlatKategoriAlat.getSelectedIndex()).getIconHealthy()).toString().replace("file:/", "").replace("%20", " ")));
                                g.translate(Integer.parseInt(jTextFieldTambahAlatPosX.getText()), Integer.parseInt(jTextFieldTambahAlatPosY.getText()));
                                if (jCheckBoxTampilkanNama.isSelected()) {
                                    /*g.setColor(new Color(255, 255, 0));
                                    Font font = new Font("Arial", Font.BOLD, 12);
                                    g.setFont(font);
                                    g.drawString(jTextFieldTambahAlatNamaAlat.getText(), 0, 0 + additionalImage.getHeight());*/
                                    g.setColor(Color.black);
                                    FontRenderContext frc = g.getFontRenderContext();
                                    if (jTextFieldTambahAlatIDAlat.getText().length() > 0) {
                                        TextLayout tl = new TextLayout(jTextFieldTambahAlatIDAlat.getText(), g.getFont().deriveFont(Float.parseFloat(jComboBoxUkuranTeks.getSelectedItem().toString())), frc);
                                        Shape shape = tl.getOutline(null);

                                        double newWidth = (Double.parseDouble(jTextFieldTambahAlatSkala.getText()) / (double) 100) * (double) additionalImage.getWidth();
                                        double newHeight = (Double.parseDouble(jTextFieldTambahAlatSkala.getText()) / (double) 100) * (double) additionalImage.getHeight();
                                        newHeight = newWidth * Math.abs(Math.sin(Math.toRadians(Double.parseDouble(jTextFieldTambahAlatRotasi.getText())))) + newHeight * Math.abs(Math.cos(Math.toRadians(Double.parseDouble(jTextFieldTambahAlatRotasi.getText()))));

                                        g.translate(-shape.getBounds2D().getWidth() / 2, newHeight / 2 + shape.getBounds2D().getHeight() + 10);
                                        g.setStroke(new BasicStroke(Float.parseFloat(jComboBoxUkuranTeks.getSelectedItem().toString()) / 5f));
                                        g.draw(shape);
                                        g.setColor(Color.white);
                                        g.fill(shape);
                                        g.translate(shape.getBounds2D().getWidth() / 2, -(newHeight / 2 + shape.getBounds2D().getHeight() + 10));
                                    }
                                }

                                g.scale((Double.parseDouble(jTextFieldTambahAlatSkala.getText()) / (double) 100), (Double.parseDouble(jTextFieldTambahAlatSkala.getText()) / (double) 100));
                                g.translate(-additionalImage.getWidth() / 2, -additionalImage.getHeight() / 2);
                                g.rotate(Math.toRadians(Float.parseFloat(jTextFieldTambahAlatRotasi.getText())), additionalImage.getWidth() / 2, additionalImage.getHeight() / 2);
                                g.drawImage(additionalImage, 0, 0, additionalImage.getWidth(), additionalImage.getHeight(), null);
                                g.rotate(Math.toRadians(-Float.parseFloat(jTextFieldTambahAlatRotasi.getText())), additionalImage.getWidth() / 2, additionalImage.getHeight() / 2);
                                g.translate(additionalImage.getWidth() / 2, additionalImage.getHeight() / 2);
                                g.scale((double) 1 / (Double.parseDouble(jTextFieldTambahAlatSkala.getText()) / (double) 100), (double) 1 / (Double.parseDouble(jTextFieldTambahAlatSkala.getText()) / (double) 100));
                                g.translate(-Integer.parseInt(jTextFieldTambahAlatPosX.getText()), -Integer.parseInt(jTextFieldTambahAlatPosY.getText()));
                            }
                        } else {
                            for (int i = 0; i < currAlats.size(); i++) {
                                if (!(currAlats.get(i).getStatusAlat() == 3 && isBlinking)) {
                                    BufferedImage additionalImage;
                                    if (jCheckBoxModaTV.isSelected()) {
                                        switch (currAlats.get(i).getStatusAlat()) {
                                            case 1:
                                                additionalImage = ImageIO.read(new File((PathHelper.path + "kategori_alat/icon_healthy/" + currAlats.get(i).getKategoriAlat().getIconHealthy()).toString().replace("file:/", "").replace("%20", " ")));
                                                break;
                                            case 2:
                                                additionalImage = ImageIO.read(new File((PathHelper.path + "kategori_alat/icon_in_repair/" + currAlats.get(i).getKategoriAlat().getIconInRepair()).toString().replace("file:/", "").replace("%20", " ")));
                                                break;
                                            case 3:
                                                additionalImage = ImageIO.read(new File((PathHelper.path + "kategori_alat/icon_damaged/" + currAlats.get(i).getKategoriAlat().getIconDamaged()).toString().replace("file:/", "").replace("%20", " ")));
                                                break;
                                            default:
                                                additionalImage = ImageIO.read(new File((PathHelper.path + "kategori_alat/icon_healthy/" + currAlats.get(i).getKategoriAlat().getIconHealthy()).toString().replace("file:/", "").replace("%20", " ")));
                                        }
                                    } else {
                                        additionalImage = ImageIO.read(new File((PathHelper.path + "kategori_alat/icon_healthy/" + currAlats.get(i).getKategoriAlat().getIconHealthy()).toString().replace("file:/", "").replace("%20", " ")));
                                    }

                                    g.translate(currAlats.get(i).getPosisiX(), currAlats.get(i).getPosisiY());
                                    if (jCheckBoxTampilkanNama.isSelected()) {
                                        /*g.setColor(new Color(255, 255, 0));
                                            Font font = new Font("Arial", Font.BOLD, 12);
                                            g.setFont(font);
                                            g.drawString(currAlats.get(i).getNamaAlat(), 0, 0 + additionalImage.getHeight());*/
                                        g.setColor(Color.black);
                                        FontRenderContext frc = g.getFontRenderContext();
                                        TextLayout tl = new TextLayout(currAlats.get(i).getIdAlat(), g.getFont().deriveFont(Float.parseFloat(jComboBoxUkuranTeks.getSelectedItem().toString())), frc);
                                        Shape shape = tl.getOutline(null);
                                        double newWidth = ((double) currAlats.get(i).getSkala() / (double) 100) * (double) additionalImage.getWidth();
                                        double newHeight = ((double) currAlats.get(i).getSkala() / (double) 100) * (double) additionalImage.getHeight();
                                        newHeight = newWidth * Math.abs(Math.sin(Math.toRadians(currAlats.get(i).getRotasi()))) + newHeight * Math.abs(Math.cos(Math.toRadians(currAlats.get(i).getRotasi())));

                                        g.translate(-shape.getBounds2D().getWidth() / 2, newHeight / 2 + shape.getBounds2D().getHeight() + 10);
                                        g.setStroke(new BasicStroke(Float.parseFloat(jComboBoxUkuranTeks.getSelectedItem().toString()) / 5f));
                                        g.draw(shape);
                                        g.setColor(Color.white);
                                        g.fill(shape);
                                        g.translate(shape.getBounds2D().getWidth() / 2, -(newHeight / 2 + shape.getBounds2D().getHeight() + 10));
                                    }
                                    g.scale(((double) currAlats.get(i).getSkala() / (double) 100), ((double) currAlats.get(i).getSkala() / (double) 100));
                                    g.translate(-additionalImage.getWidth() / 2, -additionalImage.getHeight() / 2);
                                    g.rotate(Math.toRadians(currAlats.get(i).getRotasi()), additionalImage.getWidth() / 2, additionalImage.getHeight() / 2);
                                    g.drawImage(additionalImage, 0, 0, additionalImage.getWidth(), additionalImage.getHeight(), null);
                                    g.rotate(Math.toRadians(-currAlats.get(i).getRotasi()), additionalImage.getWidth() / 2, additionalImage.getHeight() / 2);
                                    g.translate(additionalImage.getWidth() / 2, additionalImage.getHeight() / 2);
                                    g.scale((double) 1 / ((double) currAlats.get(i).getSkala() / (double) 100), (double) 1 / ((double) currAlats.get(i).getSkala() / (double) 100));
                                    g.translate(-currAlats.get(i).getPosisiX(), -currAlats.get(i).getPosisiY());
                                }
                            }
                        }
                        g.dispose();
                        Image scaledImage = newImg.getScaledInstance((int) ((float) currImageIcon2.getIconWidth() * (float) jSliderUkuranPeta.getValue() / (float) 100), (int) ((float) currImageIcon2.getIconHeight() * (float) jSliderUkuranPeta.getValue() / (float) 100), Image.SCALE_SMOOTH);
                        jLabelPeta.setIcon(new ImageIcon(scaledImage));
                    } catch (IOException ex) {
                        jLabelPeta.setIcon(null);
                    }
                } else {
                    jLabelPeta.setIcon(null);
                }
            } else {
                jLabelPeta.setIcon(null);
            }
            jSliderUkuranPeta.removeChangeListener(jSliderUkuranPetaChangeListener);
            jComboBoxUkuranPeta.removeItemListener(jComboBoxUkuranPetaItemListener);
            jComboBoxUkuranPeta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"200%", "175%", "150%", "125%", "100%", "75%", "50%", "25%", jSliderUkuranPeta.getValue() + "%"}));
            jComboBoxUkuranPeta.setSelectedIndex(jComboBoxUkuranPeta.getItemCount() - 1);
            jComboBoxUkuranPeta.addItemListener(jComboBoxUkuranPetaItemListener);
            jSliderUkuranPeta.addChangeListener(jSliderUkuranPetaChangeListener);
            isRefreshingPeta = false;
            if (isNeedToWaitRefreshingPeta) {
                isNeedToWaitRefreshingPeta = false;
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

        jFileChooserTambahAlatFotoAlat = new javax.swing.JFileChooser();
        jTabbedPaneWaletaMaintenance = new javax.swing.JTabbedPane();
        jPanelPetaMaintenance = new javax.swing.JPanel();
        jPanelPengaturanPeta = new javax.swing.JPanel();
        jComboBoxLantai = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxUkuranPeta = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jButtonKurangUkuranPeta = new javax.swing.JButton();
        jButtonTambahUkuranPeta = new javax.swing.JButton();
        jSliderUkuranPeta = new javax.swing.JSlider();
        jButtonTambahLantai2 = new javax.swing.JButton();
        jCheckBoxModaTV = new javax.swing.JCheckBox();
        jButtonSebelumnyaLantai2 = new javax.swing.JButton();
        jButtonSelanjutnyaLantai2 = new javax.swing.JButton();
        jButtonJeda = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jComboBoxUkuranTeks = new javax.swing.JComboBox<>();
        jPanelDaftarAlat = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelListDaftarAlat = new javax.swing.JPanel();
        jButtonTambahAlat = new javax.swing.JButton();
        jLabelCariAlat = new javax.swing.JLabel();
        jTextFieldCariAlat = new javax.swing.JTextField();
        jButtonHapusTerpilihAlat = new javax.swing.JButton();
        jCheckBoxPilihSemuaAlat = new javax.swing.JCheckBox();
        jToggleButtonPerbesarSemuaAlat = new javax.swing.JToggleButton();
        jLabelTerpilihAlat = new javax.swing.JLabel();
        jButtonSegarkanAlat = new javax.swing.JButton();
        jPanelTambahAlat = new javax.swing.JPanel();
        jButtonTambahAlatBatal = new javax.swing.JButton();
        jButtonTambahAlatSimpan = new javax.swing.JButton();
        jScrollPaneTambahAlat = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jComboBoxTambahAlatKategoriAlat = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButtonTambahAlatPosisiAtas = new javax.swing.JButton();
        jTextFieldTambahAlatPosY = new javax.swing.JTextField();
        jButtonTambahAlatPosisiKiri = new javax.swing.JButton();
        jButtonTambahAlatPosisiBawah = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldTambahAlatPosX = new javax.swing.JTextField();
        jButtonTambahAlatPosisiUlang = new javax.swing.JButton();
        jButtonTambahAlatPosisiKanan = new javax.swing.JButton();
        jLabelTambahAlatIDAlat = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaTambahAlatSpesifikasi = new javax.swing.JTextArea();
        jTextFieldTambahAlatIDAlat = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jButtonTambahAlatRotasiKiri = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldTambahAlatRotasi = new javax.swing.JTextField();
        jButtonTambahAlatRotasiUlang = new javax.swing.JButton();
        jButtonTambahAlatRotasiKanan = new javax.swing.JButton();
        jButtonTambahAlatRotasi90 = new javax.swing.JButton();
        jButtonTambahAlatRotasi180 = new javax.swing.JButton();
        jButtonTambahAlatRotasi270 = new javax.swing.JButton();
        jButtonTambahAlatRotasi0 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldTambahAlatMerk = new javax.swing.JTextField();
        jLabelTambahAlatKodeAlat = new javax.swing.JLabel();
        jTextFieldTambahAlatKodeAlat = new javax.swing.JTextField();
        jComboBoxTambahAlatRuangan = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jTextFieldTambahAlatSkala = new javax.swing.JTextField();
        jSliderTambahAlatSkala = new javax.swing.JSlider();
        jButtonTambahAlatSkala50 = new javax.swing.JButton();
        jButtonTambahAlatSkala200 = new javax.swing.JButton();
        jButtonTambahAlatSkalaUlang = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jLabelTambahAlatPratinjauFotoAlat = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextFieldTambahAlatFotoAlat = new javax.swing.JTextField();
        jButtonTambahAlatPilihBerkasFotoAlat = new javax.swing.JButton();
        jPanelPeta = new javax.swing.JPanel();
        jScrollPanePeta = new javax.swing.JScrollPane();
        jLabelPeta = new javax.swing.JLabel();
        jPanelLegendaPeta = new javax.swing.JPanel();
        jCheckBoxTampilkanNama = new javax.swing.JCheckBox();
        jPanelJadwalHariIni = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jPanelListDaftarJadwalHariIni = new javax.swing.JPanel();
        jPanelJadwalTerlambat = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanelListDaftarJadwalTerlambat = new javax.swing.JPanel();
        jPanelLantai = new javax.swing.JPanel();
        jPanelDataLantai = new javax.swing.JPanel();
        jScrollPaneLantai = new javax.swing.JScrollPane();
        jTableLantai = new javax.swing.JTable();
        jTextFieldCariLantai = new javax.swing.JTextField();
        jButtonTambahLantai = new javax.swing.JButton();
        jButtonEksporPDFLantai = new javax.swing.JButton();
        jButtonEksporExcelLantai = new javax.swing.JButton();
        jButtonSegarkanLantai = new javax.swing.JButton();
        jLabelCariLantai = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jButtonPertamaLantai = new javax.swing.JButton();
        jButtonSebelumnyaLantai = new javax.swing.JButton();
        jToggleButtonHal1Lantai = new javax.swing.JToggleButton();
        jToggleButtonHal2Lantai = new javax.swing.JToggleButton();
        jToggleButtonHal3Lantai = new javax.swing.JToggleButton();
        jToggleButtonHal4Lantai = new javax.swing.JToggleButton();
        jToggleButtonHal5Lantai = new javax.swing.JToggleButton();
        jButtonSelanjutnyaLantai = new javax.swing.JButton();
        jButtonTerakhirLantai = new javax.swing.JButton();
        jButtonHapusTerpilihLantai = new javax.swing.JButton();
        jLabelTerpilihLantai = new javax.swing.JLabel();
        jSeparatorAturHalamanLantai = new javax.swing.JSeparator();
        jComboBoxHalamanLantai = new javax.swing.JComboBox<>();
        jLabelHalamanLantai = new javax.swing.JLabel();
        jSeparatorHalamanLantai = new javax.swing.JSeparator();
        jComboBoxPerHalamanLantai = new javax.swing.JComboBox<>();
        jLabelPerHalamanLantai = new javax.swing.JLabel();
        jSeparatorPerHalamanLantai = new javax.swing.JSeparator();
        jToggleButtonHal6Lantai = new javax.swing.JToggleButton();
        jToggleButtonHal7Lantai = new javax.swing.JToggleButton();
        jToggleButtonHal8Lantai = new javax.swing.JToggleButton();
        jToggleButtonHal9Lantai = new javax.swing.JToggleButton();
        jToggleButtonHal10Lantai = new javax.swing.JToggleButton();
        jToggleButtonHal11Lantai = new javax.swing.JToggleButton();
        jPanelRuangan = new javax.swing.JPanel();
        jPanelDataRuangan = new javax.swing.JPanel();
        jScrollPaneRuangan = new javax.swing.JScrollPane();
        jTableRuangan = new javax.swing.JTable();
        jTextFieldCariRuangan = new javax.swing.JTextField();
        jButtonTambahRuangan = new javax.swing.JButton();
        jButtonEksporPDFRuangan = new javax.swing.JButton();
        jButtonEksporExcelRuangan = new javax.swing.JButton();
        jButtonSegarkanRuangan = new javax.swing.JButton();
        jLabelCariRuangan = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButtonPertamaRuangan = new javax.swing.JButton();
        jButtonSebelumnyaRuangan = new javax.swing.JButton();
        jToggleButtonHal1Ruangan = new javax.swing.JToggleButton();
        jToggleButtonHal2Ruangan = new javax.swing.JToggleButton();
        jToggleButtonHal3Ruangan = new javax.swing.JToggleButton();
        jToggleButtonHal4Ruangan = new javax.swing.JToggleButton();
        jToggleButtonHal5Ruangan = new javax.swing.JToggleButton();
        jButtonSelanjutnyaRuangan = new javax.swing.JButton();
        jButtonTerakhirRuangan = new javax.swing.JButton();
        jButtonHapusTerpilihRuangan = new javax.swing.JButton();
        jLabelTerpilihRuangan = new javax.swing.JLabel();
        jSeparatorAturHalamanRuangan = new javax.swing.JSeparator();
        jComboBoxHalamanRuangan = new javax.swing.JComboBox<>();
        jLabelHalamanRuangan = new javax.swing.JLabel();
        jSeparatorHalamanRuangan = new javax.swing.JSeparator();
        jComboBoxPerHalamanRuangan = new javax.swing.JComboBox<>();
        jLabelPerHalamanRuangan = new javax.swing.JLabel();
        jSeparatorPerHalamanRuangan = new javax.swing.JSeparator();
        jToggleButtonHal6Ruangan = new javax.swing.JToggleButton();
        jToggleButtonHal7Ruangan = new javax.swing.JToggleButton();
        jToggleButtonHal8Ruangan = new javax.swing.JToggleButton();
        jToggleButtonHal9Ruangan = new javax.swing.JToggleButton();
        jToggleButtonHal10Ruangan = new javax.swing.JToggleButton();
        jToggleButtonHal11Ruangan = new javax.swing.JToggleButton();
        jPanelKategoriAlat = new javax.swing.JPanel();
        jPanelDataKategoriAlat = new javax.swing.JPanel();
        jScrollPaneKategoriAlat = new javax.swing.JScrollPane();
        jTableKategoriAlat = new javax.swing.JTable();
        jTextFieldCariKategoriAlat = new javax.swing.JTextField();
        jButtonTambahKategoriAlat = new javax.swing.JButton();
        jButtonEksporPDFKategoriAlat = new javax.swing.JButton();
        jButtonEksporExcelKategoriAlat = new javax.swing.JButton();
        jButtonSegarkanKategoriAlat = new javax.swing.JButton();
        jLabelCariKategoriAlat = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButtonTerakhirKategoriAlat = new javax.swing.JButton();
        jButtonSelanjutnyaKategoriAlat = new javax.swing.JButton();
        jButtonSebelumnyaKategoriAlat = new javax.swing.JButton();
        jToggleButtonHal5KategoriAlat = new javax.swing.JToggleButton();
        jButtonPertamaKategoriAlat = new javax.swing.JButton();
        jToggleButtonHal4KategoriAlat = new javax.swing.JToggleButton();
        jToggleButtonHal3KategoriAlat = new javax.swing.JToggleButton();
        jToggleButtonHal2KategoriAlat = new javax.swing.JToggleButton();
        jToggleButtonHal1KategoriAlat = new javax.swing.JToggleButton();
        jLabelTerpilihKategoriAlat = new javax.swing.JLabel();
        jButtonHapusTerpilihKategoriAlat = new javax.swing.JButton();
        jSeparatorAturHalamanKategoriAlat = new javax.swing.JSeparator();
        jComboBoxHalamanKategoriAlat = new javax.swing.JComboBox<>();
        jLabelHalamanKategoriAlat = new javax.swing.JLabel();
        jSeparatorHalamanKategoriAlat = new javax.swing.JSeparator();
        jComboBoxPerHalamanKategoriAlat = new javax.swing.JComboBox<>();
        jLabelPerHalamanKategoriAlat = new javax.swing.JLabel();
        jSeparatorPerHalamanKategoriAlat = new javax.swing.JSeparator();
        jToggleButtonHal10KategoriAlat = new javax.swing.JToggleButton();
        jToggleButtonHal9KategoriAlat = new javax.swing.JToggleButton();
        jToggleButtonHal8KategoriAlat = new javax.swing.JToggleButton();
        jToggleButtonHal7KategoriAlat = new javax.swing.JToggleButton();
        jToggleButtonHal6KategoriAlat = new javax.swing.JToggleButton();
        jToggleButtonHal11KategoriAlat = new javax.swing.JToggleButton();
        jLabelMasukSebagai = new javax.swing.JLabel();
        jButtonKeluar = new javax.swing.JButton();
        jLabelWaktu = new javax.swing.JLabel();

        jFileChooserTambahAlatFotoAlat.setDialogTitle("Pilih Berkas Foto Alat");
        jFileChooserTambahAlatFotoAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooserTambahAlatFotoAlatActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Waleta Maintenance");

        jTabbedPaneWaletaMaintenance.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPaneWaletaMaintenance.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneWaletaMaintenanceStateChanged(evt);
            }
        });
        jTabbedPaneWaletaMaintenance.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jTabbedPaneWaletaMaintenanceMouseMoved(evt);
            }
        });

        jPanelPengaturanPeta.setBorder(javax.swing.BorderFactory.createTitledBorder("Pengaturan Peta"));

        jComboBoxLantai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-" }));
        jComboBoxLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jComboBoxLantai.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxLantaiItemStateChanged(evt);
            }
        });

        jLabel2.setText("Lantai");

        jComboBoxUkuranPeta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "200%", "175%", "150%", "125%", "100%", "75%", "50%", "25%" }));
        jComboBoxUkuranPeta.setSelectedIndex(4);
        jComboBoxUkuranPeta.setToolTipText("");
        jComboBoxUkuranPeta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel1.setText("Ukuran Peta");

        jButtonKurangUkuranPeta.setText("-");
        jButtonKurangUkuranPeta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonKurangUkuranPeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonKurangUkuranPetaActionPerformed(evt);
            }
        });

        jButtonTambahUkuranPeta.setText("+");
        jButtonTambahUkuranPeta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahUkuranPeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahUkuranPetaActionPerformed(evt);
            }
        });

        jSliderUkuranPeta.setMaximum(200);
        jSliderUkuranPeta.setValue(100);
        jSliderUkuranPeta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButtonTambahLantai2.setText("+");
        jButtonTambahLantai2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahLantai2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahLantai2ActionPerformed(evt);
            }
        });

        jCheckBoxModaTV.setText("Moda TV: OFF");
        jCheckBoxModaTV.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jCheckBoxModaTV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxModaTVActionPerformed(evt);
            }
        });

        jButtonSebelumnyaLantai2.setText("⏪");
        jButtonSebelumnyaLantai2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonSebelumnyaLantai2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSebelumnyaLantai2ActionPerformed(evt);
            }
        });

        jButtonSelanjutnyaLantai2.setText("⏩");
        jButtonSelanjutnyaLantai2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonSelanjutnyaLantai2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelanjutnyaLantai2ActionPerformed(evt);
            }
        });

        jButtonJeda.setText("⏸");
        jButtonJeda.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonJeda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonJedaActionPerformed(evt);
            }
        });

        jLabel14.setText("Ukuran Teks");

        jComboBoxUkuranTeks.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36" }));
        jComboBoxUkuranTeks.setSelectedIndex(8);
        jComboBoxUkuranTeks.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jComboBoxUkuranTeks.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxUkuranTeksItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelPengaturanPetaLayout = new javax.swing.GroupLayout(jPanelPengaturanPeta);
        jPanelPengaturanPeta.setLayout(jPanelPengaturanPetaLayout);
        jPanelPengaturanPetaLayout.setHorizontalGroup(
            jPanelPengaturanPetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPengaturanPetaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSebelumnyaLantai2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSelanjutnyaLantai2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonTambahLantai2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonJeda)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxModaTV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxUkuranTeks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxUkuranPeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonKurangUkuranPeta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSliderUkuranPeta, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonTambahUkuranPeta)
                .addContainerGap())
        );
        jPanelPengaturanPetaLayout.setVerticalGroup(
            jPanelPengaturanPetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPengaturanPetaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPengaturanPetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonTambahUkuranPeta)
                    .addComponent(jSliderUkuranPeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelPengaturanPetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jComboBoxUkuranPeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonKurangUkuranPeta)
                        .addComponent(jLabel1)
                        .addComponent(jButtonTambahLantai2)
                        .addComponent(jCheckBoxModaTV)
                        .addComponent(jButtonSebelumnyaLantai2)
                        .addComponent(jButtonSelanjutnyaLantai2)
                        .addComponent(jButtonJeda)
                        .addComponent(jLabel14)
                        .addComponent(jComboBoxUkuranTeks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jComboBoxUkuranPeta.addItemListener(jComboBoxUkuranPetaItemListener);
        jSliderUkuranPeta.addChangeListener(jSliderUkuranPetaChangeListener);

        jPanelDaftarAlat.setBorder(javax.swing.BorderFactory.createTitledBorder("Daftar Alat"));
        jPanelDaftarAlat.setPreferredSize(new java.awt.Dimension(268, 79));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jPanelListDaftarAlat.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanelListDaftarAlatLayout = new javax.swing.GroupLayout(jPanelListDaftarAlat);
        jPanelListDaftarAlat.setLayout(jPanelListDaftarAlatLayout);
        jPanelListDaftarAlatLayout.setHorizontalGroup(
            jPanelListDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 407, Short.MAX_VALUE)
        );
        jPanelListDaftarAlatLayout.setVerticalGroup(
            jPanelListDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 602, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanelListDaftarAlat);

        jButtonTambahAlat.setText("Tambah Alat");
        jButtonTambahAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatActionPerformed(evt);
            }
        });

        jLabelCariAlat.setText("Cari");

        jButtonHapusTerpilihAlat.setText("Hapus Alat Terpilih");
        jButtonHapusTerpilihAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonHapusTerpilihAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHapusTerpilihAlatActionPerformed(evt);
            }
        });

        jCheckBoxPilihSemuaAlat.setText("Pilih Semua");
        jCheckBoxPilihSemuaAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jCheckBoxPilihSemuaAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxPilihSemuaAlatActionPerformed(evt);
            }
        });

        jToggleButtonPerbesarSemuaAlat.setSelected(true);
        jToggleButtonPerbesarSemuaAlat.setText("🗖");
        jToggleButtonPerbesarSemuaAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonPerbesarSemuaAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonPerbesarSemuaAlatActionPerformed(evt);
            }
        });

        jLabelTerpilihAlat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTerpilihAlat.setText("Terpilih : 0");

        jButtonSegarkanAlat.setText("🗘");
        jButtonSegarkanAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonSegarkanAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSegarkanAlatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelDaftarAlatLayout = new javax.swing.GroupLayout(jPanelDaftarAlat);
        jPanelDaftarAlat.setLayout(jPanelDaftarAlatLayout);
        jPanelDaftarAlatLayout.setHorizontalGroup(
            jPanelDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDaftarAlatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonTambahAlat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                    .addGroup(jPanelDaftarAlatLayout.createSequentialGroup()
                        .addComponent(jLabelCariAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldCariAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSegarkanAlat))
                    .addComponent(jButtonHapusTerpilihAlat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelDaftarAlatLayout.createSequentialGroup()
                        .addComponent(jCheckBoxPilihSemuaAlat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonPerbesarSemuaAlat))
                    .addComponent(jLabelTerpilihAlat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelDaftarAlatLayout.setVerticalGroup(
            jPanelDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDaftarAlatLayout.createSequentialGroup()
                .addGroup(jPanelDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelCariAlat)
                    .addComponent(jTextFieldCariAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSegarkanAlat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDaftarAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxPilihSemuaAlat)
                    .addComponent(jToggleButtonPerbesarSemuaAlat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTerpilihAlat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonHapusTerpilihAlat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonTambahAlat)
                .addContainerGap())
        );

        jPanelTambahAlat.setBorder(javax.swing.BorderFactory.createTitledBorder("Tambah Alat"));
        jPanelTambahAlat.setEnabled(false);
        jPanelTambahAlat.setFocusable(false);
        jPanelTambahAlat.setVisible(false);

        jButtonTambahAlatBatal.setText("Batal");
        jButtonTambahAlatBatal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatBatalActionPerformed(evt);
            }
        });

        jButtonTambahAlatSimpan.setText("Simpan");
        jButtonTambahAlatSimpan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatSimpanActionPerformed(evt);
            }
        });

        jScrollPaneTambahAlat.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel3.setMaximumSize(new java.awt.Dimension(270, 902));

        jComboBoxTambahAlatKategoriAlat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxTambahAlatKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jComboBoxTambahAlatKategoriAlat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxTambahAlatKategoriAlatItemStateChanged(evt);
            }
        });

        jLabel7.setText("Spesifikasi");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Posisi"));

        jButtonTambahAlatPosisiAtas.setText("Atas");
        jButtonTambahAlatPosisiAtas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatPosisiAtas.setMaximumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiAtas.setMinimumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiAtas.setPreferredSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiAtas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonTambahAlatPosisiAtasMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonTambahAlatPosisiAtasMouseReleased(evt);
            }
        });

        jTextFieldTambahAlatPosY.setEditable(false);
        jTextFieldTambahAlatPosY.setText("0");

        jButtonTambahAlatPosisiKiri.setText("Kiri");
        jButtonTambahAlatPosisiKiri.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatPosisiKiri.setMaximumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiKiri.setMinimumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiKiri.setPreferredSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiKiri.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonTambahAlatPosisiKiriMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonTambahAlatPosisiKiriMouseReleased(evt);
            }
        });

        jButtonTambahAlatPosisiBawah.setText("Bawah");
        jButtonTambahAlatPosisiBawah.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatPosisiBawah.setMaximumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiBawah.setMinimumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiBawah.setPreferredSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiBawah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonTambahAlatPosisiBawahMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonTambahAlatPosisiBawahMouseReleased(evt);
            }
        });

        jLabel6.setText("Pos Y");

        jLabel5.setText("Pos X");

        jTextFieldTambahAlatPosX.setEditable(false);
        jTextFieldTambahAlatPosX.setText("0");

        jButtonTambahAlatPosisiUlang.setText("Ulang");
        jButtonTambahAlatPosisiUlang.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatPosisiUlang.setMaximumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiUlang.setMinimumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiUlang.setPreferredSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiUlang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatPosisiUlangActionPerformed(evt);
            }
        });

        jButtonTambahAlatPosisiKanan.setText("Kanan");
        jButtonTambahAlatPosisiKanan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatPosisiKanan.setMaximumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiKanan.setMinimumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiKanan.setPreferredSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatPosisiKanan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonTambahAlatPosisiKananMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonTambahAlatPosisiKananMouseReleased(evt);
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonTambahAlatPosisiAtas, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButtonTambahAlatPosisiKiri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonTambahAlatPosisiUlang, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonTambahAlatPosisiBawah, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonTambahAlatPosisiKanan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTambahAlatPosX))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTambahAlatPosY)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonTambahAlatPosisiAtas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonTambahAlatPosisiKiri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahAlatPosisiKanan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahAlatPosisiUlang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonTambahAlatPosisiBawah, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldTambahAlatPosX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldTambahAlatPosY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap())
        );

        jLabelTambahAlatIDAlat.setText("ID Alat");

        jLabel3.setText("Kategori Alat");

        jTextAreaTambahAlatSpesifikasi.setColumns(20);
        jTextAreaTambahAlatSpesifikasi.setRows(5);
        jScrollPane2.setViewportView(jTextAreaTambahAlatSpesifikasi);

        jTextFieldTambahAlatIDAlat.setEditable(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Rotasi"));

        jButtonTambahAlatRotasiKiri.setText("Kiri");
        jButtonTambahAlatRotasiKiri.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatRotasiKiri.setMaximumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatRotasiKiri.setMinimumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatRotasiKiri.setPreferredSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatRotasiKiri.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonTambahAlatRotasiKiriMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonTambahAlatRotasiKiriMouseReleased(evt);
            }
        });

        jLabel8.setText("Rotasi");

        jTextFieldTambahAlatRotasi.setEditable(false);
        jTextFieldTambahAlatRotasi.setText("0");

        jButtonTambahAlatRotasiUlang.setText("Ulang");
        jButtonTambahAlatRotasiUlang.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatRotasiUlang.setMaximumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatRotasiUlang.setMinimumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatRotasiUlang.setPreferredSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatRotasiUlang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatRotasiUlangActionPerformed(evt);
            }
        });

        jButtonTambahAlatRotasiKanan.setText("Kanan");
        jButtonTambahAlatRotasiKanan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatRotasiKanan.setMaximumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatRotasiKanan.setMinimumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatRotasiKanan.setPreferredSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatRotasiKanan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonTambahAlatRotasiKananMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButtonTambahAlatRotasiKananMouseReleased(evt);
            }
        });

        jButtonTambahAlatRotasi90.setText("90°");
        jButtonTambahAlatRotasi90.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatRotasi90.setMaximumSize(new java.awt.Dimension(51, 24));
        jButtonTambahAlatRotasi90.setMinimumSize(new java.awt.Dimension(51, 24));
        jButtonTambahAlatRotasi90.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatRotasi90ActionPerformed(evt);
            }
        });

        jButtonTambahAlatRotasi180.setText("180°");
        jButtonTambahAlatRotasi180.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatRotasi180.setMaximumSize(new java.awt.Dimension(51, 24));
        jButtonTambahAlatRotasi180.setMinimumSize(new java.awt.Dimension(51, 24));
        jButtonTambahAlatRotasi180.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatRotasi180ActionPerformed(evt);
            }
        });

        jButtonTambahAlatRotasi270.setText("270°");
        jButtonTambahAlatRotasi270.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatRotasi270.setMaximumSize(new java.awt.Dimension(51, 24));
        jButtonTambahAlatRotasi270.setMinimumSize(new java.awt.Dimension(51, 24));
        jButtonTambahAlatRotasi270.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatRotasi270ActionPerformed(evt);
            }
        });

        jButtonTambahAlatRotasi0.setText("0°");
        jButtonTambahAlatRotasi0.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatRotasi0.setMaximumSize(new java.awt.Dimension(51, 24));
        jButtonTambahAlatRotasi0.setMinimumSize(new java.awt.Dimension(51, 24));
        jButtonTambahAlatRotasi0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatRotasi0ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButtonTambahAlatRotasi0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTambahAlatRotasi90, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTambahAlatRotasi180, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTambahAlatRotasi270, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addGap(73, 73, 73))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButtonTambahAlatRotasiKiri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonTambahAlatRotasiUlang, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonTambahAlatRotasiKanan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldTambahAlatRotasi)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonTambahAlatRotasi90, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahAlatRotasi180, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahAlatRotasi270, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahAlatRotasi0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonTambahAlatRotasiKiri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahAlatRotasiKanan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahAlatRotasiUlang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldTambahAlatRotasi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel9.setText("Merk");

        jLabelTambahAlatKodeAlat.setText("Kode Alat");

        jComboBoxTambahAlatRuangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxTambahAlatRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel4.setText("Ruangan");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Skala"));

        jLabel13.setText("Skala");

        jTextFieldTambahAlatSkala.setEditable(false);
        jTextFieldTambahAlatSkala.setText("100");

        jSliderTambahAlatSkala.setMaximum(200);
        jSliderTambahAlatSkala.setValue(100);
        jSliderTambahAlatSkala.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jSliderTambahAlatSkala.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderTambahAlatSkalaStateChanged(evt);
            }
        });

        jButtonTambahAlatSkala50.setText("50");
        jButtonTambahAlatSkala50.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatSkala50.setMaximumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatSkala50.setMinimumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatSkala50.setPreferredSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatSkala50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatSkala50ActionPerformed(evt);
            }
        });

        jButtonTambahAlatSkala200.setText("200");
        jButtonTambahAlatSkala200.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatSkala200.setMaximumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatSkala200.setMinimumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatSkala200.setPreferredSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatSkala200.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatSkala200ActionPerformed(evt);
            }
        });

        jButtonTambahAlatSkalaUlang.setText("Ulang");
        jButtonTambahAlatSkalaUlang.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatSkalaUlang.setMaximumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatSkalaUlang.setMinimumSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatSkalaUlang.setPreferredSize(new java.awt.Dimension(70, 24));
        jButtonTambahAlatSkalaUlang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatSkalaUlangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTambahAlatSkala, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jButtonTambahAlatSkala50, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTambahAlatSkalaUlang, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonTambahAlatSkala200, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSliderTambahAlatSkala, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonTambahAlatSkala50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahAlatSkala200, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahAlatSkalaUlang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSliderTambahAlatSkala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldTambahAlatSkala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Pratinjau Foto Alat"));

        jScrollPane3.setViewportView(jLabelTambahAlatPratinjauFotoAlat);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel16.setText("Foto Alat");

        jButtonTambahAlatPilihBerkasFotoAlat.setText("Pilih Berkas");
        jButtonTambahAlatPilihBerkasFotoAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahAlatPilihBerkasFotoAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahAlatPilihBerkasFotoAlatActionPerformed(evt);
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
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxTambahAlatRuangan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabelTambahAlatKodeAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTambahAlatKodeAlat))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxTambahAlatKategoriAlat, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabelTambahAlatIDAlat)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldTambahAlatIDAlat))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldTambahAlatMerk))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextFieldTambahAlatFotoAlat, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTambahAlatPilihBerkasFotoAlat))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBoxTambahAlatRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelTambahAlatIDAlat)
                    .addComponent(jTextFieldTambahAlatIDAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldTambahAlatKodeAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTambahAlatKodeAlat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxTambahAlatKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldTambahAlatMerk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jTextFieldTambahAlatFotoAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahAlatPilihBerkasFotoAlat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPaneTambahAlat.setViewportView(jPanel3);

        javax.swing.GroupLayout jPanelTambahAlatLayout = new javax.swing.GroupLayout(jPanelTambahAlat);
        jPanelTambahAlat.setLayout(jPanelTambahAlatLayout);
        jPanelTambahAlatLayout.setHorizontalGroup(
            jPanelTambahAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTambahAlatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTambahAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonTambahAlatBatal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonTambahAlatSimpan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPaneTambahAlat))
                .addContainerGap())
        );
        jPanelTambahAlatLayout.setVerticalGroup(
            jPanelTambahAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTambahAlatLayout.createSequentialGroup()
                .addComponent(jScrollPaneTambahAlat, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonTambahAlatSimpan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonTambahAlatBatal)
                .addContainerGap())
        );

        jPanelPeta.setBorder(javax.swing.BorderFactory.createTitledBorder("Peta"));

        jLabelPeta.setBackground(new java.awt.Color(255, 255, 255));
        jLabelPeta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPeta.setOpaque(true);
        jLabelPeta.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jLabelPetaMouseDragged(evt);
            }
        });
        jLabelPeta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabelPetaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jLabelPetaMouseReleased(evt);
            }
        });
        jScrollPanePeta.setViewportView(jLabelPeta);

        javax.swing.GroupLayout jPanelPetaLayout = new javax.swing.GroupLayout(jPanelPeta);
        jPanelPeta.setLayout(jPanelPetaLayout);
        jPanelPetaLayout.setHorizontalGroup(
            jPanelPetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPetaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPanePeta)
                .addContainerGap())
        );
        jPanelPetaLayout.setVerticalGroup(
            jPanelPetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPetaLayout.createSequentialGroup()
                .addComponent(jScrollPanePeta)
                .addContainerGap())
        );

        jPanelLegendaPeta.setBorder(javax.swing.BorderFactory.createTitledBorder("Legenda Peta"));

        jCheckBoxTampilkanNama.setSelected(true);
        jCheckBoxTampilkanNama.setText("Tampilkan Nama");
        jCheckBoxTampilkanNama.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jCheckBoxTampilkanNama.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxTampilkanNamaItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanelLegendaPetaLayout = new javax.swing.GroupLayout(jPanelLegendaPeta);
        jPanelLegendaPeta.setLayout(jPanelLegendaPetaLayout);
        jPanelLegendaPetaLayout.setHorizontalGroup(
            jPanelLegendaPetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLegendaPetaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxTampilkanNama)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelLegendaPetaLayout.setVerticalGroup(
            jPanelLegendaPetaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLegendaPetaLayout.createSequentialGroup()
                .addComponent(jCheckBoxTampilkanNama)
                .addGap(0, 76, Short.MAX_VALUE))
        );

        jPanelJadwalHariIni.setBorder(javax.swing.BorderFactory.createTitledBorder("Jadwal Hari Ini"));

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jPanelListDaftarJadwalHariIni.setPreferredSize(new java.awt.Dimension(233, 100));

        javax.swing.GroupLayout jPanelListDaftarJadwalHariIniLayout = new javax.swing.GroupLayout(jPanelListDaftarJadwalHariIni);
        jPanelListDaftarJadwalHariIni.setLayout(jPanelListDaftarJadwalHariIniLayout);
        jPanelListDaftarJadwalHariIniLayout.setHorizontalGroup(
            jPanelListDaftarJadwalHariIniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
        );
        jPanelListDaftarJadwalHariIniLayout.setVerticalGroup(
            jPanelListDaftarJadwalHariIniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jScrollPane4.setViewportView(jPanelListDaftarJadwalHariIni);

        javax.swing.GroupLayout jPanelJadwalHariIniLayout = new javax.swing.GroupLayout(jPanelJadwalHariIni);
        jPanelJadwalHariIni.setLayout(jPanelJadwalHariIniLayout);
        jPanelJadwalHariIniLayout.setHorizontalGroup(
            jPanelJadwalHariIniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelJadwalHariIniLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4)
                .addContainerGap())
        );
        jPanelJadwalHariIniLayout.setVerticalGroup(
            jPanelJadwalHariIniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelJadwalHariIniLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelJadwalTerlambat.setBorder(javax.swing.BorderFactory.createTitledBorder("Jadwal Terlambat"));

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane5.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jPanelListDaftarJadwalTerlambat.setPreferredSize(new java.awt.Dimension(234, 100));

        javax.swing.GroupLayout jPanelListDaftarJadwalTerlambatLayout = new javax.swing.GroupLayout(jPanelListDaftarJadwalTerlambat);
        jPanelListDaftarJadwalTerlambat.setLayout(jPanelListDaftarJadwalTerlambatLayout);
        jPanelListDaftarJadwalTerlambatLayout.setHorizontalGroup(
            jPanelListDaftarJadwalTerlambatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 408, Short.MAX_VALUE)
        );
        jPanelListDaftarJadwalTerlambatLayout.setVerticalGroup(
            jPanelListDaftarJadwalTerlambatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jScrollPane5.setViewportView(jPanelListDaftarJadwalTerlambat);

        javax.swing.GroupLayout jPanelJadwalTerlambatLayout = new javax.swing.GroupLayout(jPanelJadwalTerlambat);
        jPanelJadwalTerlambat.setLayout(jPanelJadwalTerlambatLayout);
        jPanelJadwalTerlambatLayout.setHorizontalGroup(
            jPanelJadwalTerlambatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelJadwalTerlambatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5)
                .addContainerGap())
        );
        jPanelJadwalTerlambatLayout.setVerticalGroup(
            jPanelJadwalTerlambatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelJadwalTerlambatLayout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelPetaMaintenanceLayout = new javax.swing.GroupLayout(jPanelPetaMaintenance);
        jPanelPetaMaintenance.setLayout(jPanelPetaMaintenanceLayout);
        jPanelPetaMaintenanceLayout.setHorizontalGroup(
            jPanelPetaMaintenanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPetaMaintenanceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPetaMaintenanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelLegendaPeta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelPengaturanPeta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelPeta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelPetaMaintenanceLayout.createSequentialGroup()
                        .addComponent(jPanelJadwalHariIni, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelJadwalTerlambat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelDaftarAlat, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTambahAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelPetaMaintenanceLayout.setVerticalGroup(
            jPanelPetaMaintenanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPetaMaintenanceLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPetaMaintenanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelDaftarAlat, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                    .addGroup(jPanelPetaMaintenanceLayout.createSequentialGroup()
                        .addComponent(jPanelPeta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelPetaMaintenanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelJadwalHariIni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanelJadwalTerlambat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelLegendaPeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelPengaturanPeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanelTambahAlat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPaneWaletaMaintenance.addTab("Peta Maintenance", jPanelPetaMaintenance);

        jPanelDataLantai.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Lantai"));

        jTableLantai.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Nama Lantai", "Gambar", "Aksi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTableLantai.getTableHeader().setReorderingAllowed(false);
        jScrollPaneLantai.setViewportView(jTableLantai);
        if (jTableLantai.getColumnModel().getColumnCount() > 0) {
            jTableLantai.getColumnModel().getColumn(0).setResizable(false);
            jTableLantai.getColumnModel().getColumn(1).setResizable(false);
            jTableLantai.getColumnModel().getColumn(2).setResizable(false);
            jTableLantai.getColumnModel().getColumn(2).setPreferredWidth(130);
            jTableLantai.getColumnModel().getColumn(3).setResizable(false);
            jTableLantai.getColumnModel().getColumn(3).setPreferredWidth(130);
        }

        jButtonTambahLantai.setText("Tambah");
        jButtonTambahLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahLantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahLantaiActionPerformed(evt);
            }
        });

        jButtonEksporPDFLantai.setText("PDF");
        jButtonEksporPDFLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButtonEksporExcelLantai.setText("Excel");
        jButtonEksporExcelLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButtonSegarkanLantai.setText("🗘");
        jButtonSegarkanLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonSegarkanLantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSegarkanLantaiActionPerformed(evt);
            }
        });

        jLabelCariLantai.setText("Cari");

        jLabel12.setText("Menampilkan 1 - 5 dari 8 data");

        jButtonPertamaLantai.setText("⏮");
        jButtonPertamaLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonPertamaLantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPertamaLantaiActionPerformed(evt);
            }
        });

        jButtonSebelumnyaLantai.setText("⏪");
        jButtonSebelumnyaLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonSebelumnyaLantai.setMaximumSize(new java.awt.Dimension(26, 24));
        jButtonSebelumnyaLantai.setMinimumSize(new java.awt.Dimension(26, 24));
        jButtonSebelumnyaLantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSebelumnyaLantaiActionPerformed(evt);
            }
        });

        jToggleButtonHal1Lantai.setText("1");
        jToggleButtonHal1Lantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal1Lantai.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal1Lantai.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal1Lantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal1LantaiActionPerformed(evt);
            }
        });

        jToggleButtonHal2Lantai.setText("2");
        jToggleButtonHal2Lantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal2Lantai.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal2Lantai.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal2Lantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal2LantaiActionPerformed(evt);
            }
        });

        jToggleButtonHal3Lantai.setText("3");
        jToggleButtonHal3Lantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal3Lantai.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal3Lantai.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal3Lantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal3LantaiActionPerformed(evt);
            }
        });

        jToggleButtonHal4Lantai.setText("4");
        jToggleButtonHal4Lantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal4Lantai.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal4Lantai.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal4Lantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal4LantaiActionPerformed(evt);
            }
        });

        jToggleButtonHal5Lantai.setText("5");
        jToggleButtonHal5Lantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal5Lantai.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal5Lantai.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal5Lantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal5LantaiActionPerformed(evt);
            }
        });

        jButtonSelanjutnyaLantai.setText("⏩");
        jButtonSelanjutnyaLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonSelanjutnyaLantai.setMaximumSize(new java.awt.Dimension(26, 24));
        jButtonSelanjutnyaLantai.setMinimumSize(new java.awt.Dimension(26, 24));
        jButtonSelanjutnyaLantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelanjutnyaLantaiActionPerformed(evt);
            }
        });

        jButtonTerakhirLantai.setText("⏭");
        jButtonTerakhirLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTerakhirLantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTerakhirLantaiActionPerformed(evt);
            }
        });

        jButtonHapusTerpilihLantai.setText("Hapus Terpilih");
        jButtonHapusTerpilihLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonHapusTerpilihLantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHapusTerpilihLantaiActionPerformed(evt);
            }
        });

        jLabelTerpilihLantai.setText("Terpilih: 0");

        jSeparatorAturHalamanLantai.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jComboBoxHalamanLantai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxHalamanLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabelHalamanLantai.setText("Halaman");

        jSeparatorHalamanLantai.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jComboBoxPerHalamanLantai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5", "10", "25", "50", "100" }));
        jComboBoxPerHalamanLantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jComboBoxPerHalamanLantai.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxPerHalamanLantaiItemStateChanged(evt);
            }
        });

        jLabelPerHalamanLantai.setText("Per Halaman");

        jSeparatorPerHalamanLantai.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jToggleButtonHal6Lantai.setText("6");
        jToggleButtonHal6Lantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal6Lantai.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal6Lantai.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal6Lantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal6LantaiActionPerformed(evt);
            }
        });

        jToggleButtonHal7Lantai.setText("7");
        jToggleButtonHal7Lantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal7Lantai.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal7Lantai.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal7Lantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal7LantaiActionPerformed(evt);
            }
        });

        jToggleButtonHal8Lantai.setText("8");
        jToggleButtonHal8Lantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal8Lantai.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal8Lantai.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal8Lantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal8LantaiActionPerformed(evt);
            }
        });

        jToggleButtonHal9Lantai.setText("9");
        jToggleButtonHal9Lantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal9Lantai.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal9Lantai.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal9Lantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal9LantaiActionPerformed(evt);
            }
        });

        jToggleButtonHal10Lantai.setText("10");
        jToggleButtonHal10Lantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal10Lantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal10LantaiActionPerformed(evt);
            }
        });

        jToggleButtonHal11Lantai.setText("11");
        jToggleButtonHal11Lantai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal11Lantai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal11LantaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelDataLantaiLayout = new javax.swing.GroupLayout(jPanelDataLantai);
        jPanelDataLantai.setLayout(jPanelDataLantaiLayout);
        jPanelDataLantaiLayout.setHorizontalGroup(
            jPanelDataLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDataLantaiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDataLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneLantai, javax.swing.GroupLayout.DEFAULT_SIZE, 1589, Short.MAX_VALUE)
                    .addGroup(jPanelDataLantaiLayout.createSequentialGroup()
                        .addComponent(jLabelCariLantai)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldCariLantai, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonEksporExcelLantai)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonEksporPDFLantai)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTambahLantai)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSegarkanLantai))
                    .addGroup(jPanelDataLantaiLayout.createSequentialGroup()
                        .addGroup(jPanelDataLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabelTerpilihLantai))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 747, Short.MAX_VALUE)
                        .addGroup(jPanelDataLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDataLantaiLayout.createSequentialGroup()
                                .addComponent(jSeparatorAturHalamanLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonPertamaLantai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonSebelumnyaLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal1Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal2Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal3Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal4Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal5Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal6Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal7Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal8Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal9Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal10Lantai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal11Lantai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonSelanjutnyaLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonTerakhirLantai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparatorPerHalamanLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelPerHalamanLantai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxPerHalamanLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparatorHalamanLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelHalamanLantai)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxHalamanLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonHapusTerpilihLantai, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanelDataLantaiLayout.setVerticalGroup(
            jPanelDataLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDataLantaiLayout.createSequentialGroup()
                .addGroup(jPanelDataLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldCariLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahLantai)
                    .addComponent(jButtonEksporPDFLantai)
                    .addComponent(jButtonEksporExcelLantai)
                    .addComponent(jButtonSegarkanLantai)
                    .addComponent(jLabelCariLantai))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneLantai, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDataLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel12)
                    .addGroup(jPanelDataLantaiLayout.createSequentialGroup()
                        .addGroup(jPanelDataLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonHapusTerpilihLantai)
                            .addComponent(jLabelTerpilihLantai))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelDataLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparatorHalamanLantai)
                            .addGroup(jPanelDataLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jComboBoxHalamanLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelHalamanLantai)
                                .addComponent(jComboBoxPerHalamanLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelPerHalamanLantai))))
                    .addComponent(jSeparatorPerHalamanLantai, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelDataLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonTerakhirLantai)
                        .addComponent(jButtonSelanjutnyaLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal11Lantai)
                        .addComponent(jToggleButtonHal10Lantai)
                        .addComponent(jToggleButtonHal9Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal8Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal7Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal6Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal5Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal4Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal3Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal2Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal1Lantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonSebelumnyaLantai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonPertamaLantai))
                    .addComponent(jSeparatorAturHalamanLantai, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelLantaiLayout = new javax.swing.GroupLayout(jPanelLantai);
        jPanelLantai.setLayout(jPanelLantaiLayout);
        jPanelLantaiLayout.setHorizontalGroup(
            jPanelLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLantaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelDataLantai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelLantaiLayout.setVerticalGroup(
            jPanelLantaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLantaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelDataLantai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneWaletaMaintenance.addTab("Lantai", jPanelLantai);

        jPanelDataRuangan.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Ruangan"));

        jTableRuangan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Lantai", "Nama Ruangan", "Aksi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTableRuangan.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableRuangan.getTableHeader().setReorderingAllowed(false);
        jScrollPaneRuangan.setViewportView(jTableRuangan);
        if (jTableRuangan.getColumnModel().getColumnCount() > 0) {
            jTableRuangan.getColumnModel().getColumn(0).setResizable(false);
            jTableRuangan.getColumnModel().getColumn(1).setResizable(false);
            jTableRuangan.getColumnModel().getColumn(2).setResizable(false);
            jTableRuangan.getColumnModel().getColumn(3).setResizable(false);
            jTableRuangan.getColumnModel().getColumn(3).setPreferredWidth(130);
        }

        jButtonTambahRuangan.setText("Tambah");
        jButtonTambahRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahRuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahRuanganActionPerformed(evt);
            }
        });

        jButtonEksporPDFRuangan.setText("PDF");
        jButtonEksporPDFRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButtonEksporExcelRuangan.setText("Excel");
        jButtonEksporExcelRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButtonSegarkanRuangan.setText("🗘");
        jButtonSegarkanRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonSegarkanRuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSegarkanRuanganActionPerformed(evt);
            }
        });

        jLabelCariRuangan.setText("Cari");

        jLabel11.setText("Menampilkan 1 - 5 dari 8 data");

        jButtonPertamaRuangan.setText("⏮");
        jButtonPertamaRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonPertamaRuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPertamaRuanganActionPerformed(evt);
            }
        });

        jButtonSebelumnyaRuangan.setText("⏪");
        jButtonSebelumnyaRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonSebelumnyaRuangan.setMaximumSize(new java.awt.Dimension(26, 24));
        jButtonSebelumnyaRuangan.setMinimumSize(new java.awt.Dimension(26, 24));
        jButtonSebelumnyaRuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSebelumnyaRuanganActionPerformed(evt);
            }
        });

        jToggleButtonHal1Ruangan.setText("1");
        jToggleButtonHal1Ruangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal1Ruangan.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal1Ruangan.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal1Ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal1RuanganActionPerformed(evt);
            }
        });

        jToggleButtonHal2Ruangan.setText("2");
        jToggleButtonHal2Ruangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal2Ruangan.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal2Ruangan.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal2Ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal2RuanganActionPerformed(evt);
            }
        });

        jToggleButtonHal3Ruangan.setText("3");
        jToggleButtonHal3Ruangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal3Ruangan.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal3Ruangan.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal3Ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal3RuanganActionPerformed(evt);
            }
        });

        jToggleButtonHal4Ruangan.setText("4");
        jToggleButtonHal4Ruangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal4Ruangan.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal4Ruangan.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal4Ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal4RuanganActionPerformed(evt);
            }
        });

        jToggleButtonHal5Ruangan.setText("5");
        jToggleButtonHal5Ruangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal5Ruangan.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal5Ruangan.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal5Ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal5RuanganActionPerformed(evt);
            }
        });

        jButtonSelanjutnyaRuangan.setText("⏩");
        jButtonSelanjutnyaRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonSelanjutnyaRuangan.setMaximumSize(new java.awt.Dimension(26, 24));
        jButtonSelanjutnyaRuangan.setMinimumSize(new java.awt.Dimension(26, 24));
        jButtonSelanjutnyaRuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelanjutnyaRuanganActionPerformed(evt);
            }
        });

        jButtonTerakhirRuangan.setText("⏭");
        jButtonTerakhirRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTerakhirRuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTerakhirRuanganActionPerformed(evt);
            }
        });

        jButtonHapusTerpilihRuangan.setText("Hapus Terpilih");
        jButtonHapusTerpilihRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonHapusTerpilihRuangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHapusTerpilihRuanganActionPerformed(evt);
            }
        });

        jLabelTerpilihRuangan.setText("Terpilih: 0");

        jSeparatorAturHalamanRuangan.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jComboBoxHalamanRuangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxHalamanRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabelHalamanRuangan.setText("Halaman");

        jSeparatorHalamanRuangan.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jComboBoxPerHalamanRuangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5", "10", "25", "50", "100" }));
        jComboBoxPerHalamanRuangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jComboBoxPerHalamanRuangan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxPerHalamanRuanganItemStateChanged(evt);
            }
        });

        jLabelPerHalamanRuangan.setText("Per Halaman");

        jSeparatorPerHalamanRuangan.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jToggleButtonHal6Ruangan.setText("6");
        jToggleButtonHal6Ruangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal6Ruangan.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal6Ruangan.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal6Ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal6RuanganActionPerformed(evt);
            }
        });

        jToggleButtonHal7Ruangan.setText("7");
        jToggleButtonHal7Ruangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal7Ruangan.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal7Ruangan.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal7Ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal7RuanganActionPerformed(evt);
            }
        });

        jToggleButtonHal8Ruangan.setText("8");
        jToggleButtonHal8Ruangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal8Ruangan.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal8Ruangan.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal8Ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal8RuanganActionPerformed(evt);
            }
        });

        jToggleButtonHal9Ruangan.setText("9");
        jToggleButtonHal9Ruangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal9Ruangan.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal9Ruangan.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal9Ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal9RuanganActionPerformed(evt);
            }
        });

        jToggleButtonHal10Ruangan.setText("10");
        jToggleButtonHal10Ruangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal10Ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal10RuanganActionPerformed(evt);
            }
        });

        jToggleButtonHal11Ruangan.setText("11");
        jToggleButtonHal11Ruangan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal11Ruangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal11RuanganActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelDataRuanganLayout = new javax.swing.GroupLayout(jPanelDataRuangan);
        jPanelDataRuangan.setLayout(jPanelDataRuanganLayout);
        jPanelDataRuanganLayout.setHorizontalGroup(
            jPanelDataRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDataRuanganLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDataRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneRuangan, javax.swing.GroupLayout.DEFAULT_SIZE, 1589, Short.MAX_VALUE)
                    .addGroup(jPanelDataRuanganLayout.createSequentialGroup()
                        .addComponent(jLabelCariRuangan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldCariRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonEksporExcelRuangan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonEksporPDFRuangan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTambahRuangan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSegarkanRuangan))
                    .addGroup(jPanelDataRuanganLayout.createSequentialGroup()
                        .addGroup(jPanelDataRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabelTerpilihRuangan))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 747, Short.MAX_VALUE)
                        .addGroup(jPanelDataRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDataRuanganLayout.createSequentialGroup()
                                .addComponent(jSeparatorAturHalamanRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonPertamaRuangan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonSebelumnyaRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal1Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal2Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal3Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal4Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal5Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal6Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal7Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal8Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal9Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal10Ruangan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jToggleButtonHal11Ruangan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonSelanjutnyaRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonTerakhirRuangan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparatorPerHalamanRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelPerHalamanRuangan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxPerHalamanRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparatorHalamanRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelHalamanRuangan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxHalamanRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonHapusTerpilihRuangan, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanelDataRuanganLayout.setVerticalGroup(
            jPanelDataRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDataRuanganLayout.createSequentialGroup()
                .addGroup(jPanelDataRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldCariRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahRuangan)
                    .addComponent(jButtonEksporPDFRuangan)
                    .addComponent(jButtonEksporExcelRuangan)
                    .addComponent(jButtonSegarkanRuangan)
                    .addComponent(jLabelCariRuangan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneRuangan, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDataRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11)
                    .addGroup(jPanelDataRuanganLayout.createSequentialGroup()
                        .addGroup(jPanelDataRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonHapusTerpilihRuangan)
                            .addComponent(jLabelTerpilihRuangan))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelDataRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelDataRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jSeparatorHalamanRuangan)
                                .addGroup(jPanelDataRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBoxHalamanRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelHalamanRuangan)
                                    .addComponent(jComboBoxPerHalamanRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelPerHalamanRuangan)))
                            .addComponent(jSeparatorAturHalamanRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jSeparatorPerHalamanRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelDataRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonTerakhirRuangan)
                        .addComponent(jButtonSelanjutnyaRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal11Ruangan)
                        .addComponent(jToggleButtonHal10Ruangan)
                        .addComponent(jToggleButtonHal9Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal8Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal7Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal6Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal5Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal4Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal3Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal2Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal1Ruangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonSebelumnyaRuangan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonPertamaRuangan)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelRuanganLayout = new javax.swing.GroupLayout(jPanelRuangan);
        jPanelRuangan.setLayout(jPanelRuanganLayout);
        jPanelRuanganLayout.setHorizontalGroup(
            jPanelRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRuanganLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelDataRuangan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelRuanganLayout.setVerticalGroup(
            jPanelRuanganLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRuanganLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelDataRuangan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneWaletaMaintenance.addTab("Ruangan", jPanelRuangan);

        jPanelDataKategoriAlat.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Kategori Alat"));

        jTableKategoriAlat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "Nama Alat", "Icon Healthy", "Icon In Repair", "Icon Damaged", "Aksi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTableKategoriAlat.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableKategoriAlat.getTableHeader().setReorderingAllowed(false);
        jScrollPaneKategoriAlat.setViewportView(jTableKategoriAlat);
        if (jTableKategoriAlat.getColumnModel().getColumnCount() > 0) {
            jTableKategoriAlat.getColumnModel().getColumn(0).setResizable(false);
            jTableKategoriAlat.getColumnModel().getColumn(1).setResizable(false);
            jTableKategoriAlat.getColumnModel().getColumn(2).setResizable(false);
            jTableKategoriAlat.getColumnModel().getColumn(2).setPreferredWidth(130);
            jTableKategoriAlat.getColumnModel().getColumn(3).setResizable(false);
            jTableKategoriAlat.getColumnModel().getColumn(3).setPreferredWidth(130);
            jTableKategoriAlat.getColumnModel().getColumn(4).setResizable(false);
            jTableKategoriAlat.getColumnModel().getColumn(4).setPreferredWidth(130);
            jTableKategoriAlat.getColumnModel().getColumn(5).setResizable(false);
            jTableKategoriAlat.getColumnModel().getColumn(5).setPreferredWidth(130);
        }

        jButtonTambahKategoriAlat.setText("Tambah");
        jButtonTambahKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTambahKategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTambahKategoriAlatActionPerformed(evt);
            }
        });

        jButtonEksporPDFKategoriAlat.setText("PDF");
        jButtonEksporPDFKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButtonEksporExcelKategoriAlat.setText("Excel");
        jButtonEksporExcelKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButtonSegarkanKategoriAlat.setText("🗘");
        jButtonSegarkanKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonSegarkanKategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSegarkanKategoriAlatActionPerformed(evt);
            }
        });

        jLabelCariKategoriAlat.setText("Cari");

        jLabel10.setText("Menampilkan 1 - 5 dari 8 data");

        jButtonTerakhirKategoriAlat.setText("⏭");
        jButtonTerakhirKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonTerakhirKategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTerakhirKategoriAlatActionPerformed(evt);
            }
        });

        jButtonSelanjutnyaKategoriAlat.setText("⏩");
        jButtonSelanjutnyaKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonSelanjutnyaKategoriAlat.setMaximumSize(new java.awt.Dimension(26, 24));
        jButtonSelanjutnyaKategoriAlat.setMinimumSize(new java.awt.Dimension(26, 24));
        jButtonSelanjutnyaKategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelanjutnyaKategoriAlatActionPerformed(evt);
            }
        });

        jButtonSebelumnyaKategoriAlat.setText("⏪");
        jButtonSebelumnyaKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonSebelumnyaKategoriAlat.setMaximumSize(new java.awt.Dimension(26, 24));
        jButtonSebelumnyaKategoriAlat.setMinimumSize(new java.awt.Dimension(26, 24));
        jButtonSebelumnyaKategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSebelumnyaKategoriAlatActionPerformed(evt);
            }
        });

        jToggleButtonHal5KategoriAlat.setText("5");
        jToggleButtonHal5KategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal5KategoriAlat.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal5KategoriAlat.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal5KategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal5KategoriAlatActionPerformed(evt);
            }
        });

        jButtonPertamaKategoriAlat.setText("⏮");
        jButtonPertamaKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonPertamaKategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPertamaKategoriAlatActionPerformed(evt);
            }
        });

        jToggleButtonHal4KategoriAlat.setText("4");
        jToggleButtonHal4KategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal4KategoriAlat.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal4KategoriAlat.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal4KategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal4KategoriAlatActionPerformed(evt);
            }
        });

        jToggleButtonHal3KategoriAlat.setText("3");
        jToggleButtonHal3KategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal3KategoriAlat.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal3KategoriAlat.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal3KategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal3KategoriAlatActionPerformed(evt);
            }
        });

        jToggleButtonHal2KategoriAlat.setText("2");
        jToggleButtonHal2KategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal2KategoriAlat.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal2KategoriAlat.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal2KategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal2KategoriAlatActionPerformed(evt);
            }
        });

        jToggleButtonHal1KategoriAlat.setText("1");
        jToggleButtonHal1KategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal1KategoriAlat.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal1KategoriAlat.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal1KategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal1KategoriAlatActionPerformed(evt);
            }
        });

        jLabelTerpilihKategoriAlat.setText("Terpilih: 0");

        jButtonHapusTerpilihKategoriAlat.setText("Hapus Terpilih");
        jButtonHapusTerpilihKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButtonHapusTerpilihKategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHapusTerpilihKategoriAlatActionPerformed(evt);
            }
        });

        jSeparatorAturHalamanKategoriAlat.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jComboBoxHalamanKategoriAlat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxHalamanKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabelHalamanKategoriAlat.setText("Halaman");

        jSeparatorHalamanKategoriAlat.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jComboBoxPerHalamanKategoriAlat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5", "10", "25", "50", "100" }));
        jComboBoxPerHalamanKategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jComboBoxPerHalamanKategoriAlat.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxPerHalamanKategoriAlatItemStateChanged(evt);
            }
        });

        jLabelPerHalamanKategoriAlat.setText("Per Halaman");

        jSeparatorPerHalamanKategoriAlat.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jToggleButtonHal10KategoriAlat.setText("10");
        jToggleButtonHal10KategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal10KategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal10KategoriAlatActionPerformed(evt);
            }
        });

        jToggleButtonHal9KategoriAlat.setText("9");
        jToggleButtonHal9KategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal9KategoriAlat.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal9KategoriAlat.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal9KategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal9KategoriAlatActionPerformed(evt);
            }
        });

        jToggleButtonHal8KategoriAlat.setText("8");
        jToggleButtonHal8KategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal8KategoriAlat.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal8KategoriAlat.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal8KategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal8KategoriAlatActionPerformed(evt);
            }
        });

        jToggleButtonHal7KategoriAlat.setText("7");
        jToggleButtonHal7KategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal7KategoriAlat.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal7KategoriAlat.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal7KategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal7KategoriAlatActionPerformed(evt);
            }
        });

        jToggleButtonHal6KategoriAlat.setText("6");
        jToggleButtonHal6KategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal6KategoriAlat.setMaximumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal6KategoriAlat.setMinimumSize(new java.awt.Dimension(26, 24));
        jToggleButtonHal6KategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal6KategoriAlatActionPerformed(evt);
            }
        });

        jToggleButtonHal11KategoriAlat.setText("11");
        jToggleButtonHal11KategoriAlat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jToggleButtonHal11KategoriAlat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHal11KategoriAlatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelDataKategoriAlatLayout = new javax.swing.GroupLayout(jPanelDataKategoriAlat);
        jPanelDataKategoriAlat.setLayout(jPanelDataKategoriAlatLayout);
        jPanelDataKategoriAlatLayout.setHorizontalGroup(
            jPanelDataKategoriAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDataKategoriAlatLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelDataKategoriAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneKategoriAlat)
                    .addGroup(jPanelDataKategoriAlatLayout.createSequentialGroup()
                        .addComponent(jLabelCariKategoriAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldCariKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonEksporExcelKategoriAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonEksporPDFKategoriAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTambahKategoriAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSegarkanKategoriAlat))
                    .addGroup(jPanelDataKategoriAlatLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 747, Short.MAX_VALUE)
                        .addComponent(jSeparatorAturHalamanKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonPertamaKategoriAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSebelumnyaKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonHal1KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonHal2KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonHal3KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonHal4KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonHal5KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonHal6KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonHal7KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonHal8KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonHal9KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonHal10KategoriAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jToggleButtonHal11KategoriAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSelanjutnyaKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonTerakhirKategoriAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparatorPerHalamanKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelPerHalamanKategoriAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxPerHalamanKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparatorHalamanKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelHalamanKategoriAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxHalamanKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelDataKategoriAlatLayout.createSequentialGroup()
                        .addComponent(jLabelTerpilihKategoriAlat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonHapusTerpilihKategoriAlat)))
                .addContainerGap())
        );
        jPanelDataKategoriAlatLayout.setVerticalGroup(
            jPanelDataKategoriAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelDataKategoriAlatLayout.createSequentialGroup()
                .addGroup(jPanelDataKategoriAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldCariKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTambahKategoriAlat)
                    .addComponent(jButtonEksporPDFKategoriAlat)
                    .addComponent(jButtonEksporExcelKategoriAlat)
                    .addComponent(jButtonSegarkanKategoriAlat)
                    .addComponent(jLabelCariKategoriAlat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneKategoriAlat, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelDataKategoriAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10)
                    .addGroup(jPanelDataKategoriAlatLayout.createSequentialGroup()
                        .addGroup(jPanelDataKategoriAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonHapusTerpilihKategoriAlat)
                            .addComponent(jLabelTerpilihKategoriAlat))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelDataKategoriAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparatorHalamanKategoriAlat)
                            .addGroup(jPanelDataKategoriAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jComboBoxHalamanKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelHalamanKategoriAlat)
                                .addComponent(jComboBoxPerHalamanKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelPerHalamanKategoriAlat))))
                    .addComponent(jSeparatorPerHalamanKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelDataKategoriAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonTerakhirKategoriAlat)
                        .addComponent(jButtonSelanjutnyaKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal11KategoriAlat)
                        .addComponent(jToggleButtonHal10KategoriAlat)
                        .addComponent(jToggleButtonHal9KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal8KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal7KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal6KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal5KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal4KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal3KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal2KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jToggleButtonHal1KategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonSebelumnyaKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonPertamaKategoriAlat))
                    .addComponent(jSeparatorAturHalamanKategoriAlat, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanelKategoriAlatLayout = new javax.swing.GroupLayout(jPanelKategoriAlat);
        jPanelKategoriAlat.setLayout(jPanelKategoriAlatLayout);
        jPanelKategoriAlatLayout.setHorizontalGroup(
            jPanelKategoriAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelKategoriAlatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelDataKategoriAlat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelKategoriAlatLayout.setVerticalGroup(
            jPanelKategoriAlatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelKategoriAlatLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelDataKategoriAlat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPaneWaletaMaintenance.addTab("Kategori Alat", jPanelKategoriAlat);

        jLabelMasukSebagai.setText("Masuk Sebagai: -");

        jButtonKeluar.setText("Keluar");
        jButtonKeluar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabelWaktu.setText("Waktu");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelWaktu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelMasukSebagai)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonKeluar)
                .addContainerGap())
            .addComponent(jTabbedPaneWaletaMaintenance)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonKeluar)
                    .addComponent(jLabelMasukSebagai)
                    .addComponent(jLabelWaktu))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPaneWaletaMaintenance))
        );

        setSize(new java.awt.Dimension(1641, 690));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxLantaiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxLantaiItemStateChanged
        if (evt.getID() == ItemEvent.ITEM_STATE_CHANGED) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                JComboBox<String> cb = (JComboBox<String>) evt.getSource();
                if (cb.getSelectedIndex() == 0) {
                    currImageIcon = null;
                    jPanelDaftarAlat.setVisible(false);
                } else {
                    try {
                        currImageIcon = new javax.swing.ImageIcon((PathHelper.path + "lantai/" + lantais.get(cb.getSelectedIndex() - 1).getGambar()));
                    } catch (NullPointerException e) {
                        currImageIcon = null;
                    }
                    jCheckBoxPilihSemuaAlat.setSelected(false);
                    jCheckBoxPilihSemuaAlat.setText("Pilih Semua");
                    jToggleButtonPerbesarSemuaAlat.setSelected(true);
                    jToggleButtonPerbesarSemuaAlat.setText("🗖");
                    jLabelTerpilihAlat.setVisible(false);
                    jButtonHapusTerpilihAlat.setVisible(false);
                    jPanelDaftarAlat.setVisible(true);
                }
                refreshAlat();
            }
        }
    }//GEN-LAST:event_jComboBoxLantaiItemStateChanged

    private void jButtonKurangUkuranPetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonKurangUkuranPetaActionPerformed
        int newValue = jSliderUkuranPeta.getValue();
        if (newValue % 10 > 0) {
            newValue -= (newValue % 10);
        } else if (newValue - 10 < 0) {
            newValue = 0;
        } else {
            newValue -= 10;
        }
        jSliderUkuranPeta.setValue(newValue);
    }//GEN-LAST:event_jButtonKurangUkuranPetaActionPerformed

    private void jButtonTambahUkuranPetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahUkuranPetaActionPerformed
        int newValue = jSliderUkuranPeta.getValue();
        if (newValue % 10 > 0) {
            newValue += (10 - (newValue % 10));
        } else if (newValue + 10 > 200) {
            newValue = 200;
        } else {
            newValue += 10;
        }
        jSliderUkuranPeta.setValue(newValue);
    }//GEN-LAST:event_jButtonTambahUkuranPetaActionPerformed

    private void jTabbedPaneWaletaMaintenanceMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPaneWaletaMaintenanceMouseMoved
        TabbedPaneUI ui = jTabbedPaneWaletaMaintenance.getUI();
        int index = ui.tabForCoordinate(jTabbedPaneWaletaMaintenance, evt.getX(), evt.getY());
        if (index >= 0) {
            jTabbedPaneWaletaMaintenance.setCursor(new Cursor((Cursor.HAND_CURSOR)));
        } else {
            jTabbedPaneWaletaMaintenance.setCursor(null);
        }
    }//GEN-LAST:event_jTabbedPaneWaletaMaintenanceMouseMoved

    private void jTabbedPaneWaletaMaintenanceStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneWaletaMaintenanceStateChanged
        setTitle("Waleta Maintenance - " + jTabbedPaneWaletaMaintenance.getTitleAt(jTabbedPaneWaletaMaintenance.getSelectedIndex()));
        switch (jTabbedPaneWaletaMaintenance.getSelectedIndex()) {
            case 0:
                //refreshPeta(currImageIcon, alats);
                refreshAlat();
                break;
            case 1:
                refreshLantai();
                break;
            case 2:
                refreshRuangan();
                break;
            case 3:
                refreshKategoriAlat();
                break;
        }
    }//GEN-LAST:event_jTabbedPaneWaletaMaintenanceStateChanged

    private void jButtonTambahRuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahRuanganActionPerformed
        new TambahUbahRuanganJDialog(this, true, MainFrame_Maintenance.this, TambahUbahRuanganJDialog.KategoriDialog.TAMBAH).setVisible(true);
    }//GEN-LAST:event_jButtonTambahRuanganActionPerformed

    private void jButtonSegarkanRuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSegarkanRuanganActionPerformed
        refreshRuangan();
    }//GEN-LAST:event_jButtonSegarkanRuanganActionPerformed

    private void jButtonTambahKategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahKategoriAlatActionPerformed
        new TambahUbahKategoriAlatJDialog(this, true, MainFrame_Maintenance.this, TambahUbahKategoriAlatJDialog.KategoriDialog.TAMBAH).setVisible(true);
    }//GEN-LAST:event_jButtonTambahKategoriAlatActionPerformed

    private void jButtonSegarkanKategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSegarkanKategoriAlatActionPerformed
        refreshKategoriAlat();
    }//GEN-LAST:event_jButtonSegarkanKategoriAlatActionPerformed

    private void jButtonTambahAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatActionPerformed
        isInsertOrEditAlat = true;
        jPanelDaftarAlat.setFocusable(false);
        jPanelDaftarAlat.setEnabled(false);
        jPanelDaftarAlat.setVisible(false);
        kategoriDialogAlat = KategoriDialogAlat.TAMBAH;
        jLabelTambahAlatIDAlat.setVisible(false);
        jTextFieldTambahAlatIDAlat.setText("Alat Baru");
        jTextFieldTambahAlatIDAlat.setVisible(false);
        jTextFieldTambahAlatKodeAlat.setText("");
        jLabelTambahAlatKodeAlat.setVisible(true);
        jTextFieldTambahAlatKodeAlat.setVisible(true);
        jComboBoxTambahAlatKategoriAlat.setSelectedIndex(-1);
        jTextFieldTambahAlatPosX.setText("0");
        jTextFieldTambahAlatPosY.setText("0");
        jTextFieldTambahAlatRotasi.setText("0");
        jTextFieldTambahAlatSkala.setText("100");
        jSliderTambahAlatSkala.setValue(100);
        jTextAreaTambahAlatSpesifikasi.setText("");
        jTextFieldTambahAlatFotoAlat.setText("");
        jLabelTambahAlatPratinjauFotoAlat.setIcon(null);
        jScrollPaneTambahAlat.getVerticalScrollBar().setValue(jScrollPaneTambahAlat.getVerticalScrollBar().getMinimum());
        jPanelTambahAlat.setBorder(javax.swing.BorderFactory.createTitledBorder("Tambah Alat"));
        jPanelTambahAlat.setFocusable(true);
        jPanelTambahAlat.setEnabled(true);
        jPanelTambahAlat.setVisible(true);
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jButtonTambahAlatActionPerformed

    public void jButtonUbahAlatActionPerformed(int idAlat) {
        idAlatUpdate = idAlat;
        isInsertOrEditAlat = true;
        jPanelDaftarAlat.setFocusable(false);
        jPanelDaftarAlat.setEnabled(false);
        jPanelDaftarAlat.setVisible(false);
        kategoriDialogAlat = KategoriDialogAlat.UBAH;
        jTextFieldTambahAlatIDAlat.setText(alats.get(idAlat).getIdAlat());
        jLabelTambahAlatIDAlat.setVisible(true);
        jTextFieldTambahAlatIDAlat.setVisible(true);
        jLabelTambahAlatKodeAlat.setVisible(false);
        jTextFieldTambahAlatKodeAlat.setVisible(false);
        jTextFieldTambahAlatMerk.setText(alats.get(idAlat).getMerk());
        for (int i = 0; i < kategoriAlats.size(); i++) {
            if (kategoriAlats.get(i).getIdKategoriAlat() == alats.get(idAlat).getKategoriAlat().getIdKategoriAlat()) {
                jComboBoxTambahAlatKategoriAlat.setSelectedIndex(i);
                break;
            }
        }
        jTextFieldTambahAlatPosX.setText(Integer.toString(alats.get(idAlat).getPosisiX()));
        jTextFieldTambahAlatPosY.setText(Integer.toString(alats.get(idAlat).getPosisiY()));
        jTextFieldTambahAlatRotasi.setText(Float.toString(alats.get(idAlat).getRotasi()));
        jTextFieldTambahAlatSkala.setText(Integer.toString(alats.get(idAlat).getSkala()));
        jSliderTambahAlatSkala.setValue(alats.get(idAlat).getSkala());
        jTextAreaTambahAlatSpesifikasi.setText(alats.get(idAlat).getSpesifikasi());
        jTextFieldTambahAlatFotoAlat.setText(alats.get(idAlat).getFotoAlat());
        javax.swing.ImageIcon currImageIconFotoAlat;
        if (alats.get(idAlat).getFotoAlat() == null) {
            currImageIconFotoAlat = null;
        } else {
            currImageIconFotoAlat = new javax.swing.ImageIcon((PathHelper.path + "alat/" + alats.get(idAlat).getFotoAlat()));
        }
        jLabelTambahAlatPratinjauFotoAlat.setIcon(currImageIconFotoAlat);
        jScrollPaneTambahAlat.getVerticalScrollBar().setValue(jScrollPaneTambahAlat.getVerticalScrollBar().getMinimum());
        jPanelTambahAlat.setBorder(javax.swing.BorderFactory.createTitledBorder("Ubah Alat"));
        jPanelTambahAlat.setFocusable(true);
        jPanelTambahAlat.setEnabled(true);
        jPanelTambahAlat.setVisible(true);
        refreshPeta(currImageIcon, alats);
    }

    public void jButtonHapusAlatActionPerformed(int idAlat) {
        String namaAlat = alats.get(idAlat).getIdAlat();
        int opsi = JOptionPane.showConfirmDialog(MainFrame_Maintenance.this, "Apakah anda yakin akan menghapus alat \"" + namaAlat + "\"?", "Menghapus Data Alat", JOptionPane.YES_NO_OPTION);
        if (opsi == JOptionPane.YES_OPTION) {
            try {
                if (new AlatDAO().delete(MainFrame_Maintenance.this, alats.get(idAlat).getIdAlat())) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Sukses menghapus alat \"" + namaAlat + "\"");
                    refreshRuangan();
                } else {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus alat");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus alat: " + ex.getLocalizedMessage() + "");
            }

        }
    }

    public void jToggleButtonPerkecilActionPerformed(int idAlat) {
        int totalSelected = 0;
        for (int i = 0; i < daftarAlatJPanels.size(); i++) {
            if ((boolean) daftarAlatJPanels.get(i).getjToggleButtonPerkecil().isSelected()) {
                totalSelected++;
            }
        }
        if (totalSelected == daftarAlatJPanels.size()) {
            jToggleButtonPerbesarSemuaAlat.setSelected(true);
            jToggleButtonPerbesarSemuaAlat.setText("🗖");
        } else {
            jToggleButtonPerbesarSemuaAlat.setSelected(false);
            jToggleButtonPerbesarSemuaAlat.setText("🗕");
        }
    }

    public void jCheckBoxPilihAlatActionPerformed(int idAlat) {
        int totalSelected = 0;
        for (int i = 0; i < daftarAlatJPanels.size(); i++) {
            if ((boolean) daftarAlatJPanels.get(i).getjCheckBoxPilih().isSelected()) {
                daftarAlatJPanels.get(i).getjCheckBoxPilih().setText("Batal Pilih");
                totalSelected++;
            } else {
                daftarAlatJPanels.get(i).getjCheckBoxPilih().setText("Pilih");
            }
        }
        if (totalSelected == daftarAlatJPanels.size()) {
            jCheckBoxPilihSemuaAlat.setSelected(true);
            jCheckBoxPilihSemuaAlat.setText("Batal Pilih Semua");
        } else {
            jCheckBoxPilihSemuaAlat.setSelected(false);
            jCheckBoxPilihSemuaAlat.setText("Pilih Semua");
        }

        if (totalSelected > 0) {
            jLabelTerpilihAlat.setText("Terpilih: " + totalSelected);
            jLabelTerpilihAlat.setVisible(true);
            jButtonHapusTerpilihAlat.setVisible(true);
        } else {
            jLabelTerpilihAlat.setVisible(false);
            jButtonHapusTerpilihAlat.setVisible(false);
        }
    }

    private void jButtonTambahAlatBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatBatalActionPerformed
        isInsertOrEditAlat = false;
        jPanelDaftarAlat.setFocusable(true);
        jPanelDaftarAlat.setEnabled(true);
        jPanelDaftarAlat.setVisible(true);
        jPanelTambahAlat.setFocusable(false);
        jPanelTambahAlat.setEnabled(false);
        jPanelTambahAlat.setVisible(false);
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jButtonTambahAlatBatalActionPerformed

    private void jButtonTambahAlatSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatSimpanActionPerformed
        switch (kategoriDialogAlat) {
            case TAMBAH:
                try {
                    Alat newAlat = new Alat(jTextFieldTambahAlatKodeAlat.getText(), kategoriAlats.get(jComboBoxTambahAlatKategoriAlat.getSelectedIndex()), ruangans.get(jComboBoxTambahAlatRuangan.getSelectedIndex()), jTextFieldTambahAlatMerk.getText(), jTextAreaTambahAlatSpesifikasi.getText(), jTextFieldTambahAlatFotoAlat.getText(), 1, Integer.parseInt(jTextFieldTambahAlatPosX.getText()), Integer.parseInt(jTextFieldTambahAlatPosY.getText()), Float.parseFloat(jTextFieldTambahAlatRotasi.getText()), Integer.parseInt(jTextFieldTambahAlatSkala.getText()));
                    if (new AlatDAO().insert(MainFrame_Maintenance.this, newAlat)) {
                        JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Sukses menyimpan alat", "Sukses Simpan", JOptionPane.INFORMATION_MESSAGE);
                        isInsertOrEditAlat = false;
                        jPanelDaftarAlat.setFocusable(true);
                        jPanelDaftarAlat.setEnabled(true);
                        jPanelDaftarAlat.setVisible(true);
                        jPanelTambahAlat.setFocusable(false);
                        jPanelTambahAlat.setEnabled(false);
                        jPanelTambahAlat.setVisible(false);
                        refreshAlat();
                    } else {
                        JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyimpan alat", "Sukses Simpan", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyimpan alat: " + e.getLocalizedMessage(), "Gagal Simpan", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case UBAH:
                try {
                    Alat newAlat = new Alat(jTextFieldTambahAlatIDAlat.getText(), kategoriAlats.get(jComboBoxTambahAlatKategoriAlat.getSelectedIndex()), ruangans.get(jComboBoxTambahAlatRuangan.getSelectedIndex()), jTextFieldTambahAlatMerk.getText(), jTextAreaTambahAlatSpesifikasi.getText(), jTextFieldTambahAlatFotoAlat.getText(), alats.get(idAlatUpdate).getStatusAlat(), Integer.parseInt(jTextFieldTambahAlatPosX.getText()), Integer.parseInt(jTextFieldTambahAlatPosY.getText()), Float.parseFloat(jTextFieldTambahAlatRotasi.getText()), Integer.parseInt(jTextFieldTambahAlatSkala.getText()));
                    if (new AlatDAO().update(MainFrame_Maintenance.this, alats.get(idAlatUpdate).getIdAlat(), newAlat)) {
                        JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Sukses menyimpan alat", "Sukses Simpan", JOptionPane.INFORMATION_MESSAGE);
                        isInsertOrEditAlat = false;
                        jPanelDaftarAlat.setFocusable(true);
                        jPanelDaftarAlat.setEnabled(true);
                        jPanelDaftarAlat.setVisible(true);
                        jPanelTambahAlat.setFocusable(false);
                        jPanelTambahAlat.setEnabled(false);
                        jPanelTambahAlat.setVisible(false);
                        newAlat = new AlatDAO().selectWhereIdAlat(alats.get(idAlatUpdate).getIdAlat());
                        alats.set(idAlatUpdate, newAlat);
                        if (jComboBoxTambahAlatKategoriAlat.getSelectedIndex() >= 0) {
                            daftarAlatJPanels.get(idAlatUpdate).getjLabelKategoriAlat().setText(kategoriAlats.get(jComboBoxTambahAlatKategoriAlat.getSelectedIndex()).getNamaAlat());
                        } else {
                            daftarAlatJPanels.get(idAlatUpdate).getjLabelKategoriAlat().setText("-");
                        }
                        if (jTextFieldTambahAlatMerk.getText().length() > 0) {
                            daftarAlatJPanels.get(idAlatUpdate).getjLabelMerk().setText(jTextFieldTambahAlatMerk.getText());
                        } else {
                            daftarAlatJPanels.get(idAlatUpdate).getjLabelMerk().setText("-");
                        }
                        daftarAlatJPanels.get(idAlatUpdate).getjTextAreaSpesifikasi().setText(jTextAreaTambahAlatSpesifikasi.getText());
                        refreshPeta(currImageIcon, alats);
                    } else {
                        JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyimpan alat", "Sukses Simpan", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyimpan alat: " + e.getLocalizedMessage(), "Gagal Simpan", JOptionPane.ERROR_MESSAGE);
                }
                break;
        }
    }//GEN-LAST:event_jButtonTambahAlatSimpanActionPerformed

    private void jButtonTambahAlatPosisiUlangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatPosisiUlangActionPerformed
        jTextFieldTambahAlatPosX.setText("0");
        jTextFieldTambahAlatPosY.setText("0");
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jButtonTambahAlatPosisiUlangActionPerformed

    private void jButtonTambahAlatRotasiUlangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatRotasiUlangActionPerformed
        jTextFieldTambahAlatRotasi.setText("0");
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jButtonTambahAlatRotasiUlangActionPerformed

    private void jComboBoxTambahAlatKategoriAlatItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxTambahAlatKategoriAlatItemStateChanged
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jComboBoxTambahAlatKategoriAlatItemStateChanged

    private void jCheckBoxTampilkanNamaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxTampilkanNamaItemStateChanged
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jCheckBoxTampilkanNamaItemStateChanged

    private void jButtonTambahAlatPosisiKananMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTambahAlatPosisiKananMousePressed
        timerPosisiKanan = new Timer();
        timerPosisiKanan.schedule(new TimerTask() {
            @Override
            public void run() {
                jTextFieldTambahAlatPosX.setText(Integer.toString(Integer.parseInt(jTextFieldTambahAlatPosX.getText()) + 1));
                refreshPeta(currImageIcon, alats);
            }
        }, 0, 25);
    }//GEN-LAST:event_jButtonTambahAlatPosisiKananMousePressed

    private void jButtonTambahAlatPosisiKananMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTambahAlatPosisiKananMouseReleased
        timerPosisiKanan.cancel();
    }//GEN-LAST:event_jButtonTambahAlatPosisiKananMouseReleased

    private void jButtonTambahAlatPosisiKiriMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTambahAlatPosisiKiriMousePressed
        timerPosisiKiri = new Timer();
        timerPosisiKiri.schedule(new TimerTask() {
            @Override
            public void run() {
                jTextFieldTambahAlatPosX.setText(Integer.toString(Integer.parseInt(jTextFieldTambahAlatPosX.getText()) - 1));
                refreshPeta(currImageIcon, alats);
            }
        }, 0, 25);
    }//GEN-LAST:event_jButtonTambahAlatPosisiKiriMousePressed

    private void jButtonTambahAlatPosisiKiriMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTambahAlatPosisiKiriMouseReleased
        timerPosisiKiri.cancel();
    }//GEN-LAST:event_jButtonTambahAlatPosisiKiriMouseReleased

    private void jButtonTambahAlatPosisiAtasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTambahAlatPosisiAtasMousePressed
        timerPosisiAtas = new Timer();
        timerPosisiAtas.schedule(new TimerTask() {
            @Override
            public void run() {
                jTextFieldTambahAlatPosY.setText(Integer.toString(Integer.parseInt(jTextFieldTambahAlatPosY.getText()) - 1));
                refreshPeta(currImageIcon, alats);
            }
        }, 0, 25);
    }//GEN-LAST:event_jButtonTambahAlatPosisiAtasMousePressed

    private void jButtonTambahAlatPosisiAtasMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTambahAlatPosisiAtasMouseReleased
        timerPosisiAtas.cancel();
    }//GEN-LAST:event_jButtonTambahAlatPosisiAtasMouseReleased

    private void jButtonTambahAlatPosisiBawahMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTambahAlatPosisiBawahMousePressed
        timerPosisiBawah = new Timer();
        timerPosisiBawah.schedule(new TimerTask() {
            @Override
            public void run() {
                jTextFieldTambahAlatPosY.setText(Integer.toString(Integer.parseInt(jTextFieldTambahAlatPosY.getText()) + 1));
                refreshPeta(currImageIcon, alats);
            }
        }, 0, 25);
    }//GEN-LAST:event_jButtonTambahAlatPosisiBawahMousePressed

    private void jButtonTambahAlatPosisiBawahMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTambahAlatPosisiBawahMouseReleased
        timerPosisiBawah.cancel();
    }//GEN-LAST:event_jButtonTambahAlatPosisiBawahMouseReleased

    private void jButtonTambahAlatRotasiKiriMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTambahAlatRotasiKiriMousePressed
        timerRotasiKiri = new Timer();
        timerRotasiKiri.schedule(new TimerTask() {
            @Override
            public void run() {
                jTextFieldTambahAlatRotasi.setText(Float.toString(Float.parseFloat(jTextFieldTambahAlatRotasi.getText()) - 1));
                refreshPeta(currImageIcon, alats);
            }
        }, 0, 25);
    }//GEN-LAST:event_jButtonTambahAlatRotasiKiriMousePressed

    private void jButtonTambahAlatRotasiKiriMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTambahAlatRotasiKiriMouseReleased
        timerRotasiKiri.cancel();
    }//GEN-LAST:event_jButtonTambahAlatRotasiKiriMouseReleased

    private void jButtonTambahAlatRotasiKananMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTambahAlatRotasiKananMousePressed
        timerRotasiKanan = new Timer();
        timerRotasiKanan.schedule(new TimerTask() {
            @Override
            public void run() {
                jTextFieldTambahAlatRotasi.setText(Float.toString(Float.parseFloat(jTextFieldTambahAlatRotasi.getText()) + 1));
                refreshPeta(currImageIcon, alats);
            }
        }, 0, 25);
    }//GEN-LAST:event_jButtonTambahAlatRotasiKananMousePressed

    private void jButtonTambahAlatRotasiKananMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTambahAlatRotasiKananMouseReleased
        timerRotasiKanan.cancel();
    }//GEN-LAST:event_jButtonTambahAlatRotasiKananMouseReleased
    private Point origin;
    private void jLabelPetaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelPetaMousePressed
        if (isInsertOrEditAlat) {
            int pengurangWidth = 0;
            int pengurangHeight = 0;
            if (jLabelPeta.getIcon() != null) {
                pengurangWidth = jLabelPeta.getWidth() - jLabelPeta.getIcon().getIconWidth();
                if (pengurangWidth > 0) {
                    pengurangWidth /= 2;
                } else {
                    pengurangWidth = 0;
                }
                pengurangHeight = jLabelPeta.getHeight() - jLabelPeta.getIcon().getIconHeight();
                if (pengurangHeight > 0) {
                    pengurangHeight /= 2;
                } else {
                    pengurangHeight = 0;
                }
            }
            jTextFieldTambahAlatPosX.setText(Integer.toString((int) (((float) evt.getX() - pengurangWidth) / (float) jSliderUkuranPeta.getValue() * (float) 100)));
            jTextFieldTambahAlatPosY.setText(Integer.toString((int) (((float) evt.getY() - pengurangHeight) / (float) jSliderUkuranPeta.getValue() * (float) 100)));
            refreshPeta(currImageIcon, alats);
        }
        origin = new Point(evt.getPoint());
        jLabelPeta.setCursor(new java.awt.Cursor(java.awt.Cursor.MOVE_CURSOR));
        isMouseDown = true;
    }//GEN-LAST:event_jLabelPetaMousePressed

    private void jLabelPetaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelPetaMouseReleased
        jLabelPeta.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        isMouseDown = false;
    }//GEN-LAST:event_jLabelPetaMouseReleased

    private void jLabelPetaMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelPetaMouseDragged
        if (isMouseDown) {
            if (isInsertOrEditAlat) {
                int pengurangWidth = 0;
                int pengurangHeight = 0;
                if (jLabelPeta.getIcon() != null) {
                    pengurangWidth = jLabelPeta.getWidth() - jLabelPeta.getIcon().getIconWidth();
                    if (pengurangWidth > 0) {
                        pengurangWidth /= 2;
                    } else {
                        pengurangWidth = 0;
                    }
                    pengurangHeight = jLabelPeta.getHeight() - jLabelPeta.getIcon().getIconHeight();
                    if (pengurangHeight > 0) {
                        pengurangHeight /= 2;
                    } else {
                        pengurangHeight = 0;
                    }
                }
                jTextFieldTambahAlatPosX.setText(Integer.toString((int) (((float) evt.getX() - pengurangWidth) / (float) jSliderUkuranPeta.getValue() * (float) 100)));
                jTextFieldTambahAlatPosY.setText(Integer.toString((int) (((float) evt.getY() - pengurangHeight) / (float) jSliderUkuranPeta.getValue() * (float) 100)));
                refreshPeta(currImageIcon, alats);
            } else {
                if (origin != null) {
                    JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, jLabelPeta);
                    if (viewPort != null) {
                        int deltaX = origin.x - evt.getX();
                        int deltaY = origin.y - evt.getY();

                        Rectangle view = viewPort.getViewRect();
                        view.x += deltaX;
                        view.y += deltaY;

                        jLabelPeta.scrollRectToVisible(view);
                    }
                }
            }
        }
    }//GEN-LAST:event_jLabelPetaMouseDragged

    private void jButtonTambahLantai2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahLantai2ActionPerformed
        new TambahUbahLantaiJDialog(this, true, MainFrame_Maintenance.this, TambahUbahLantaiJDialog.KategoriDialog.TAMBAH).setVisible(true);
    }//GEN-LAST:event_jButtonTambahLantai2ActionPerformed

    private void jButtonHapusTerpilihRuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHapusTerpilihRuanganActionPerformed
        int opsi = JOptionPane.showConfirmDialog(MainFrame_Maintenance.this, "Apakah anda yakin akan menghapus ruangan terpilih?", "Menghapus Data Ruangan", JOptionPane.YES_NO_OPTION);
        if (opsi == JOptionPane.YES_OPTION) {
            try {
                ArrayList<String> idRuanganDelete = new ArrayList<String>();
                for (int i = 0; i < jTableRuangan.getRowCount(); i++) {
                    if ((boolean) jTableRuangan.getValueAt(i, 0)) {
                        idRuanganDelete.add(ruangans.get(i).getIdRuangan());
                    }
                }
                if (new RuanganDAO().deleteSelected(MainFrame_Maintenance.this, idRuanganDelete)) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Sukses menghapus ruangan terpilih");
                    refreshRuangan();
                } else {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus ruangan terpilih");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus ruangan terpilih: " + ex.getLocalizedMessage() + "");
            }

        }
    }//GEN-LAST:event_jButtonHapusTerpilihRuanganActionPerformed

    private void jButtonHapusTerpilihKategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHapusTerpilihKategoriAlatActionPerformed
        int opsi = JOptionPane.showConfirmDialog(MainFrame_Maintenance.this, "Apakah anda yakin akan menghapus kategori alat terpilih?", "Menghapus Data Kategori Alat", JOptionPane.YES_NO_OPTION);
        if (opsi == JOptionPane.YES_OPTION) {
            try {
                ArrayList<Integer> idKategoriAlatDelete = new ArrayList<Integer>();
                for (int i = 0; i < jTableKategoriAlat.getRowCount(); i++) {
                    if ((boolean) jTableKategoriAlat.getValueAt(i, 0)) {
                        idKategoriAlatDelete.add(kategoriAlats.get(i).getIdKategoriAlat());
                    }
                }
                if (new KategoriAlatDAO().deleteSelected(MainFrame_Maintenance.this, idKategoriAlatDelete)) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Sukses menghapus kategori alat terpilih");
                    refreshKategoriAlat();
                } else {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus kategori alat terpilih");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus kategori alat terpilih: " + ex.getLocalizedMessage() + "");
            }

        }
    }//GEN-LAST:event_jButtonHapusTerpilihKategoriAlatActionPerformed

    private void jToggleButtonPerbesarSemuaAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonPerbesarSemuaAlatActionPerformed
        for (int i = 0; i < daftarAlatJPanels.size(); i++) {
            daftarAlatJPanels.get(i).getjToggleButtonPerkecil().setSelected(jToggleButtonPerbesarSemuaAlat.isSelected());
            daftarAlatJPanels.get(i).checkJToggleButtonPerkecil(jToggleButtonPerbesarSemuaAlat.isSelected());
        }

        if (jToggleButtonPerbesarSemuaAlat.isSelected()) {
            jToggleButtonPerbesarSemuaAlat.setText("🗖");
        } else {
            jToggleButtonPerbesarSemuaAlat.setText("🗕");
        }
    }//GEN-LAST:event_jToggleButtonPerbesarSemuaAlatActionPerformed

    private void jCheckBoxPilihSemuaAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxPilihSemuaAlatActionPerformed
        for (int i = 0; i < daftarAlatJPanels.size(); i++) {
            daftarAlatJPanels.get(i).getjCheckBoxPilih().setSelected(jCheckBoxPilihSemuaAlat.isSelected());
            if (jCheckBoxPilihSemuaAlat.isSelected()) {
                daftarAlatJPanels.get(i).getjCheckBoxPilih().setText("Batal Pilih");
            } else {
                daftarAlatJPanels.get(i).getjCheckBoxPilih().setText("Pilih");
            }
        }

        if (jCheckBoxPilihSemuaAlat.isSelected()) {
            jCheckBoxPilihSemuaAlat.setText("Batal Pilih Semua");
            jLabelTerpilihAlat.setText("Terpilih: " + daftarAlatJPanels.size());
            jLabelTerpilihAlat.setVisible(true);
            jButtonHapusTerpilihAlat.setVisible(true);
        } else {
            jCheckBoxPilihSemuaAlat.setText("Pilih Semua");
            jLabelTerpilihAlat.setVisible(false);
            jButtonHapusTerpilihAlat.setVisible(false);
        }
    }//GEN-LAST:event_jCheckBoxPilihSemuaAlatActionPerformed

    private void jButtonSegarkanAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSegarkanAlatActionPerformed
        refreshAlat();
    }//GEN-LAST:event_jButtonSegarkanAlatActionPerformed

    private void jButtonTambahLantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahLantaiActionPerformed
        new TambahUbahLantaiJDialog(this, true, MainFrame_Maintenance.this, TambahUbahLantaiJDialog.KategoriDialog.TAMBAH).setVisible(true);
    }//GEN-LAST:event_jButtonTambahLantaiActionPerformed

    private void jButtonSegarkanLantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSegarkanLantaiActionPerformed
        refreshLantai();
    }//GEN-LAST:event_jButtonSegarkanLantaiActionPerformed

    private void jButtonHapusTerpilihLantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHapusTerpilihLantaiActionPerformed
        int opsi = JOptionPane.showConfirmDialog(MainFrame_Maintenance.this, "Apakah anda yakin akan menghapus lantai terpilih?", "Menghapus Data Lantai", JOptionPane.YES_NO_OPTION);
        if (opsi == JOptionPane.YES_OPTION) {
            try {
                ArrayList<String> idLantaiDelete = new ArrayList<String>();
                for (int i = 0; i < jTableLantai.getRowCount(); i++) {
                    if ((boolean) jTableLantai.getValueAt(i, 0)) {
                        idLantaiDelete.add(lantais.get(i).getIdLantai());
                    }
                }
                if (new LantaiDAO().deleteSelected(MainFrame_Maintenance.this, idLantaiDelete)) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Sukses menghapus lantai terpilih");
                    refreshRuangan();
                } else {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus lantai terpilih");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus lantai terpilih: " + ex.getLocalizedMessage() + "");
            }

        }
    }//GEN-LAST:event_jButtonHapusTerpilihLantaiActionPerformed

    private void jButtonPertamaLantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPertamaLantaiActionPerformed
        currPageLantai = 1;
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonPertamaLantaiActionPerformed

    private void jButtonSebelumnyaLantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSebelumnyaLantaiActionPerformed
        currPageLantai -= 1;
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonSebelumnyaLantaiActionPerformed

    private void jToggleButtonHal1LantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal1LantaiActionPerformed
        currPageLantai = pageLantai.get(0);
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal1LantaiActionPerformed

    private void jToggleButtonHal2LantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal2LantaiActionPerformed
        currPageLantai = pageLantai.get(1);
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal2LantaiActionPerformed

    private void jToggleButtonHal3LantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal3LantaiActionPerformed
        currPageLantai = pageLantai.get(2);
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal3LantaiActionPerformed

    private void jToggleButtonHal4LantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal4LantaiActionPerformed
        currPageLantai = pageLantai.get(3);
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal4LantaiActionPerformed

    private void jToggleButtonHal5LantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal5LantaiActionPerformed
        currPageLantai = pageLantai.get(4);
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal5LantaiActionPerformed

    private void jToggleButtonHal6LantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal6LantaiActionPerformed
        currPageLantai = pageLantai.get(5);
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal6LantaiActionPerformed

    private void jToggleButtonHal7LantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal7LantaiActionPerformed
        currPageLantai = pageLantai.get(6);
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal7LantaiActionPerformed

    private void jToggleButtonHal8LantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal8LantaiActionPerformed
        currPageLantai = pageLantai.get(7);
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal8LantaiActionPerformed

    private void jToggleButtonHal9LantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal9LantaiActionPerformed
        currPageLantai = pageLantai.get(8);
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal9LantaiActionPerformed

    private void jToggleButtonHal10LantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal10LantaiActionPerformed
        currPageLantai = pageLantai.get(9);
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal10LantaiActionPerformed

    private void jToggleButtonHal11LantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal11LantaiActionPerformed
        currPageLantai = pageLantai.get(10);
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal11LantaiActionPerformed

    private void jButtonSelanjutnyaLantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelanjutnyaLantaiActionPerformed
        currPageLantai += 1;
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonSelanjutnyaLantaiActionPerformed

    private void jButtonTerakhirLantaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTerakhirLantaiActionPerformed
        currPageLantai = totalPageLantai;
        try {
            refreshJTableLantai();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonTerakhirLantaiActionPerformed

    private void jComboBoxPerHalamanLantaiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxPerHalamanLantaiItemStateChanged
        if (evt.getID() == ItemEvent.ITEM_STATE_CHANGED) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {
                    refreshJTableLantai();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jComboBoxPerHalamanLantaiItemStateChanged

    private void jComboBoxHalamanLantaiItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getID() == ItemEvent.ITEM_STATE_CHANGED) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {
                    currPageLantai = Integer.parseInt(jComboBoxHalamanLantai.getSelectedItem().toString());
                } catch (NumberFormatException e) {
                    currPageLantai = 0;
                }
                try {
                    refreshJTableLantai();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Lantai: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void jButtonPertamaRuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPertamaRuanganActionPerformed
        currPageRuangan = 1;
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonPertamaRuanganActionPerformed

    private void jButtonSebelumnyaRuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSebelumnyaRuanganActionPerformed
        currPageRuangan -= 1;
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonSebelumnyaRuanganActionPerformed

    private void jToggleButtonHal1RuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal1RuanganActionPerformed
        currPageRuangan = pageRuangan.get(0);
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal1RuanganActionPerformed

    private void jToggleButtonHal2RuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal2RuanganActionPerformed
        currPageRuangan = pageRuangan.get(1);
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal2RuanganActionPerformed

    private void jToggleButtonHal3RuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal3RuanganActionPerformed
        currPageRuangan = pageRuangan.get(2);
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal3RuanganActionPerformed

    private void jToggleButtonHal4RuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal4RuanganActionPerformed
        currPageRuangan = pageRuangan.get(3);
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal4RuanganActionPerformed

    private void jToggleButtonHal5RuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal5RuanganActionPerformed
        currPageRuangan = pageRuangan.get(4);
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal5RuanganActionPerformed

    private void jToggleButtonHal6RuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal6RuanganActionPerformed
        currPageRuangan = pageRuangan.get(5);
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal6RuanganActionPerformed

    private void jToggleButtonHal7RuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal7RuanganActionPerformed
        currPageRuangan = pageRuangan.get(6);
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal7RuanganActionPerformed

    private void jToggleButtonHal8RuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal8RuanganActionPerformed
        currPageRuangan = pageRuangan.get(7);
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal8RuanganActionPerformed

    private void jToggleButtonHal9RuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal9RuanganActionPerformed
        currPageRuangan = pageRuangan.get(8);
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal9RuanganActionPerformed

    private void jToggleButtonHal10RuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal10RuanganActionPerformed
        currPageRuangan = pageRuangan.get(9);
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal10RuanganActionPerformed

    private void jToggleButtonHal11RuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal11RuanganActionPerformed
        currPageRuangan = pageRuangan.get(10);
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal11RuanganActionPerformed

    private void jButtonSelanjutnyaRuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelanjutnyaRuanganActionPerformed
        currPageRuangan += 1;
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonSelanjutnyaRuanganActionPerformed

    private void jButtonTerakhirRuanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTerakhirRuanganActionPerformed
        currPageRuangan = totalPageRuangan;
        try {
            refreshJTableRuangan();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonTerakhirRuanganActionPerformed

    private void jComboBoxPerHalamanRuanganItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxPerHalamanRuanganItemStateChanged
        if (evt.getID() == ItemEvent.ITEM_STATE_CHANGED) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {
                    currPageRuangan = Integer.parseInt(jComboBoxHalamanRuangan.getSelectedItem().toString());
                } catch (NumberFormatException e) {
                    currPageRuangan = 0;
                }
                try {
                    refreshJTableRuangan();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jComboBoxPerHalamanRuanganItemStateChanged

    private void jComboBoxHalamanRuanganItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getID() == ItemEvent.ITEM_STATE_CHANGED) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {
                    currPageRuangan = Integer.parseInt(jComboBoxHalamanRuangan.getSelectedItem().toString());
                } catch (NumberFormatException e) {
                    currPageRuangan = 0;
                }
                try {
                    refreshJTableRuangan();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Ruangan: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void jButtonPertamaKategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPertamaKategoriAlatActionPerformed
        currPageKategoriAlat = 1;
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonPertamaKategoriAlatActionPerformed

    private void jButtonSebelumnyaKategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSebelumnyaKategoriAlatActionPerformed
        currPageKategoriAlat -= 1;
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonSebelumnyaKategoriAlatActionPerformed

    private void jToggleButtonHal1KategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal1KategoriAlatActionPerformed
        currPageKategoriAlat = pageKategoriAlat.get(0);
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal1KategoriAlatActionPerformed

    private void jToggleButtonHal2KategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal2KategoriAlatActionPerformed
        currPageKategoriAlat = pageKategoriAlat.get(1);
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal2KategoriAlatActionPerformed

    private void jToggleButtonHal3KategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal3KategoriAlatActionPerformed
        currPageKategoriAlat = pageKategoriAlat.get(2);
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal3KategoriAlatActionPerformed

    private void jToggleButtonHal4KategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal4KategoriAlatActionPerformed
        currPageKategoriAlat = pageKategoriAlat.get(3);
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal4KategoriAlatActionPerformed

    private void jToggleButtonHal5KategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal5KategoriAlatActionPerformed
        currPageKategoriAlat = pageKategoriAlat.get(4);
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal5KategoriAlatActionPerformed

    private void jToggleButtonHal6KategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal6KategoriAlatActionPerformed
        currPageKategoriAlat = pageKategoriAlat.get(5);
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal6KategoriAlatActionPerformed

    private void jToggleButtonHal7KategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal7KategoriAlatActionPerformed
        currPageKategoriAlat = pageKategoriAlat.get(6);
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal7KategoriAlatActionPerformed

    private void jToggleButtonHal8KategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal8KategoriAlatActionPerformed
        currPageKategoriAlat = pageKategoriAlat.get(7);
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal8KategoriAlatActionPerformed

    private void jToggleButtonHal9KategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal9KategoriAlatActionPerformed
        currPageKategoriAlat = pageKategoriAlat.get(8);
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal9KategoriAlatActionPerformed

    private void jToggleButtonHal10KategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal10KategoriAlatActionPerformed
        currPageKategoriAlat = pageKategoriAlat.get(9);
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal10KategoriAlatActionPerformed

    private void jToggleButtonHal11KategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHal11KategoriAlatActionPerformed
        currPageKategoriAlat = pageKategoriAlat.get(10);
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jToggleButtonHal11KategoriAlatActionPerformed

    private void jButtonSelanjutnyaKategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelanjutnyaKategoriAlatActionPerformed
        currPageKategoriAlat += 1;
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonSelanjutnyaKategoriAlatActionPerformed

    private void jButtonTerakhirKategoriAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTerakhirKategoriAlatActionPerformed
        currPageKategoriAlat = totalPageKategoriAlat;
        try {
            refreshJTableKategoriAlat();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonTerakhirKategoriAlatActionPerformed

    private void jComboBoxPerHalamanKategoriAlatItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxPerHalamanKategoriAlatItemStateChanged
        if (evt.getID() == ItemEvent.ITEM_STATE_CHANGED) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {
                    currPageKategoriAlat = Integer.parseInt(jComboBoxHalamanKategoriAlat.getSelectedItem().toString());
                } catch (NumberFormatException e) {
                    currPageKategoriAlat = 0;
                }
                try {
                    refreshJTableKategoriAlat();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_jComboBoxPerHalamanKategoriAlatItemStateChanged

    private void jCheckBoxModaTVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxModaTVActionPerformed
        if (jCheckBoxModaTV.isSelected()) {
            jCheckBoxModaTV.setText("Moda TV: ON");
            jLabelWaktu.setVisible(false);
            jLabelMasukSebagai.setVisible(false);
            jButtonKeluar.setVisible(false);
            jButtonJeda.setText("⏸");
            jButtonJeda.setVisible(true);
            jButtonTambahLantai2.setVisible(false);
            jPanelJadwalHariIni.setVisible(true);
            jPanelJadwalTerlambat.setVisible(true);
            jPanelLegendaPeta.setVisible(false);
            jButtonTambahAlat.setVisible(false);
            jCheckBoxPilihSemuaAlat.setVisible(false);
            jToggleButtonPerbesarSemuaAlat.setVisible(false);
            jTabbedPaneWaletaMaintenance.setUI(new BasicTabbedPaneUI() {
                @Override
                protected int calculateTabAreaHeight(int tab_placement, int run_count, int max_tab_height) {
                    return 0;
                }
            });
            refreshAlat();
            refreshJadwalHariIni();
            refreshJadwalTerlambat();
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
//            this.setUndecorated(true);
            timerTV = new Timer(true);
            timerTaskTV = new TimerTask() {
                @Override
                public void run() {
                    if (jComboBoxLantai.getItemCount() > 0) {
                        if (jCheckBoxModaTV.isSelected()) {
                            if (jComboBoxLantai.getSelectedIndex() >= jComboBoxLantai.getItemCount() - 1) {
                                jComboBoxLantai.setSelectedIndex(1);
                            } else {
                                jComboBoxLantai.setSelectedIndex(jComboBoxLantai.getSelectedIndex() + 1);
                            }
                        } else {
                            if (jComboBoxLantai.getSelectedIndex() >= jComboBoxLantai.getItemCount() - 1) {
                                jComboBoxLantai.setSelectedIndex(0);
                            } else {
                                jComboBoxLantai.setSelectedIndex(jComboBoxLantai.getSelectedIndex() + 1);
                            }
                        }
                    }
                }
            };
            timerTV.scheduleAtFixedRate(timerTaskTV, 10 * 1000, 10 * 1000);
            timerTVBlink = new Timer(true);
            isBlinking = true;
            timerTaskTVBlink = new TimerTask() {
                @Override
                public void run() {
                    isBlinking = !isBlinking;
                    isNeedToWaitRefreshingPeta = true;
                    refreshPeta(currImageIcon, alats);
                }
            };
            timerTVBlink.scheduleAtFixedRate(timerTaskTVBlink, 0, 1000);
            if (jComboBoxLantai.getItemCount() > 0) {
                if (jComboBoxLantai.getSelectedIndex() == 0) {
                    jComboBoxLantai.setSelectedIndex(1);
                }
            }

            jScrollPane5.getHorizontalScrollBar().setValue(0);
            jScrollPane4.getHorizontalScrollBar().setValue(0);
            timerScrollJadwal = new Timer(true);
            timerTaskScrollJadwal = new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (jScrollPane5.getHorizontalScrollBar().getValue() + 5 < jScrollPane5.getHorizontalScrollBar().getMaximum()) {
                                jScrollPane5.getHorizontalScrollBar().setValue(jScrollPane5.getHorizontalScrollBar().getValue() + 5);
                            } else {
                                jScrollPane5.getHorizontalScrollBar().setValue(0);
                            }
                            if (jScrollPane4.getHorizontalScrollBar().getValue() + 5 < jScrollPane4.getHorizontalScrollBar().getMaximum()) {
                                jScrollPane4.getHorizontalScrollBar().setValue(jScrollPane4.getHorizontalScrollBar().getValue() + 5);
                            } else {
                                jScrollPane4.getHorizontalScrollBar().setValue(0);
                            }
                        }
                    });
                }
            };
            timerScrollJadwal.scheduleAtFixedRate(timerTaskScrollJadwal, 0, 10 * 20);
        } else {
            jCheckBoxModaTV.setText("Moda TV: OFF");
            jLabelWaktu.setVisible(true);
            jLabelMasukSebagai.setVisible(true);
            jButtonKeluar.setVisible(true);
            jButtonJeda.setVisible(false);
            jButtonTambahLantai2.setVisible(true);
            jPanelJadwalHariIni.setVisible(false);
            jPanelJadwalTerlambat.setVisible(false);
            jPanelLegendaPeta.setVisible(true);
            jButtonTambahAlat.setVisible(true);
            jCheckBoxPilihSemuaAlat.setVisible(true);
            jToggleButtonPerbesarSemuaAlat.setVisible(true);
            jTabbedPaneWaletaMaintenance.setUI(new BasicTabbedPaneUI() {
                @Override
                protected int calculateTabAreaHeight(int tab_placement, int run_count, int max_tab_height) {
                    return super.calculateTabAreaWidth(tab_placement, run_count, max_tab_height - 13);
                }

            });
            refreshAlat();
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
//            this.setUndecorated(false);
            timerTV.cancel();
            timerTaskTV.cancel();
            timerTVBlink.cancel();
            timerTaskTVBlink.cancel();
            timerScrollJadwal.cancel();
            timerTaskScrollJadwal.cancel();
        }
    }//GEN-LAST:event_jCheckBoxModaTVActionPerformed

    private void jButtonHapusTerpilihAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHapusTerpilihAlatActionPerformed
        int opsi = JOptionPane.showConfirmDialog(MainFrame_Maintenance.this, "Apakah anda yakin akan menghapus alat terpilih?", "Menghapus Data Alat", JOptionPane.YES_NO_OPTION);
        if (opsi == JOptionPane.YES_OPTION) {
            try {
                ArrayList<String> idAlatDelete = new ArrayList<String>();
                for (int i = 0; i < daftarAlatJPanels.size(); i++) {
                    if (daftarAlatJPanels.get(i).getjCheckBoxPilih().isSelected()) {
                        idAlatDelete.add(alats.get(i).getIdAlat());
                    }
                }
                if (new AlatDAO().deleteSelected(MainFrame_Maintenance.this, idAlatDelete)) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Sukses menghapus alat terpilih");
                    refreshAlat();
                } else {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus alat terpilih");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menghapus alat terpilih: " + ex.getLocalizedMessage() + "");
            }

        }
    }//GEN-LAST:event_jButtonHapusTerpilihAlatActionPerformed

    private void jButtonSebelumnyaLantai2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSebelumnyaLantai2ActionPerformed
        if (jComboBoxLantai.getItemCount() > 0) {
            if (jCheckBoxModaTV.isSelected()) {
                if (jComboBoxLantai.getSelectedIndex() <= 1) {
                    jComboBoxLantai.setSelectedIndex(jComboBoxLantai.getItemCount() - 1);
                } else {
                    jComboBoxLantai.setSelectedIndex(jComboBoxLantai.getSelectedIndex() - 1);
                }
            } else {
                if (jComboBoxLantai.getSelectedIndex() <= 0) {
                    jComboBoxLantai.setSelectedIndex(jComboBoxLantai.getItemCount() - 1);
                } else {
                    jComboBoxLantai.setSelectedIndex(jComboBoxLantai.getSelectedIndex() - 1);
                }
            }
        }
        if (jButtonJeda.getText().equals("⏸")) {
            jButtonJeda.setText("▶");
            if (timerTV != null) {
                timerTV.cancel();
                timerTaskTV.cancel();
            }
            refreshPeta(currImageIcon, alats);
        }
    }//GEN-LAST:event_jButtonSebelumnyaLantai2ActionPerformed

    private void jButtonSelanjutnyaLantai2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelanjutnyaLantai2ActionPerformed
        if (jComboBoxLantai.getItemCount() > 0) {
            if (jCheckBoxModaTV.isSelected()) {
                if (jComboBoxLantai.getSelectedIndex() >= jComboBoxLantai.getItemCount() - 1) {
                    jComboBoxLantai.setSelectedIndex(1);
                } else {
                    jComboBoxLantai.setSelectedIndex(jComboBoxLantai.getSelectedIndex() + 1);
                }
            } else {
                if (jComboBoxLantai.getSelectedIndex() >= jComboBoxLantai.getItemCount() - 1) {
                    jComboBoxLantai.setSelectedIndex(0);
                } else {
                    jComboBoxLantai.setSelectedIndex(jComboBoxLantai.getSelectedIndex() + 1);
                }
            }
        }
        if (jButtonJeda.getText().equals("⏸")) {
            jButtonJeda.setText("▶");
            if (timerTV != null) {
                timerTV.cancel();
                timerTaskTV.cancel();
            }
            refreshPeta(currImageIcon, alats);
        }
    }//GEN-LAST:event_jButtonSelanjutnyaLantai2ActionPerformed

    private void jButtonTambahAlatRotasi0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatRotasi0ActionPerformed
        jTextFieldTambahAlatRotasi.setText("0");
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jButtonTambahAlatRotasi0ActionPerformed

    private void jButtonTambahAlatRotasi90ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatRotasi90ActionPerformed
        jTextFieldTambahAlatRotasi.setText("90");
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jButtonTambahAlatRotasi90ActionPerformed

    private void jButtonTambahAlatRotasi180ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatRotasi180ActionPerformed
        jTextFieldTambahAlatRotasi.setText("180");
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jButtonTambahAlatRotasi180ActionPerformed

    private void jButtonTambahAlatRotasi270ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatRotasi270ActionPerformed
        jTextFieldTambahAlatRotasi.setText("270");
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jButtonTambahAlatRotasi270ActionPerformed

    private void jButtonJedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonJedaActionPerformed
        if (jButtonJeda.getText().equals("⏸")) {
            jButtonJeda.setText("▶");
            timerTV.cancel();
            timerTaskTV.cancel();
            refreshPeta(currImageIcon, alats);
        } else {
            jButtonJeda.setText("⏸");
            timerTV = new Timer(true);
            timerTaskTV = new TimerTask() {
                @Override
                public void run() {
                    if (jComboBoxLantai.getItemCount() > 0) {
                        if (jCheckBoxModaTV.isSelected()) {
                            if (jComboBoxLantai.getSelectedIndex() >= jComboBoxLantai.getItemCount() - 1) {
                                jComboBoxLantai.setSelectedIndex(1);
                            } else {
                                jComboBoxLantai.setSelectedIndex(jComboBoxLantai.getSelectedIndex() + 1);
                            }
                        } else {
                            if (jComboBoxLantai.getSelectedIndex() >= jComboBoxLantai.getItemCount() - 1) {
                                jComboBoxLantai.setSelectedIndex(0);
                            } else {
                                jComboBoxLantai.setSelectedIndex(jComboBoxLantai.getSelectedIndex() + 1);
                            }
                        }
                    }
                }
            };
            timerTV.scheduleAtFixedRate(timerTaskTV, 10 * 1000, 10 * 1000);
        }
    }//GEN-LAST:event_jButtonJedaActionPerformed

    private void jButtonTambahAlatPilihBerkasFotoAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatPilihBerkasFotoAlatActionPerformed
        jFileChooserTambahAlatFotoAlat.showDialog(MainFrame_Maintenance.this, "Pilih");
    }//GEN-LAST:event_jButtonTambahAlatPilihBerkasFotoAlatActionPerformed

    private void jFileChooserTambahAlatFotoAlatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooserTambahAlatFotoAlatActionPerformed
        jTextFieldTambahAlatFotoAlat.setText(jFileChooserTambahAlatFotoAlat.getSelectedFile().getAbsolutePath());
        javax.swing.ImageIcon currImageIcon;
        if (jFileChooserTambahAlatFotoAlat.getSelectedFile().getAbsolutePath() == null) {
            currImageIcon = null;
        } else {
            currImageIcon = new javax.swing.ImageIcon(jFileChooserTambahAlatFotoAlat.getSelectedFile().getAbsolutePath());
        }
        jLabelTambahAlatPratinjauFotoAlat.setIcon(currImageIcon);
    }//GEN-LAST:event_jFileChooserTambahAlatFotoAlatActionPerformed

    private void jButtonTambahAlatSkala50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatSkala50ActionPerformed
        jSliderTambahAlatSkala.setValue(50);
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jButtonTambahAlatSkala50ActionPerformed

    private void jButtonTambahAlatSkala200ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatSkala200ActionPerformed
        jSliderTambahAlatSkala.setValue(200);
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jButtonTambahAlatSkala200ActionPerformed

    private void jButtonTambahAlatSkalaUlangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTambahAlatSkalaUlangActionPerformed
        jSliderTambahAlatSkala.setValue(100);
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jButtonTambahAlatSkalaUlangActionPerformed

    private void jSliderTambahAlatSkalaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderTambahAlatSkalaStateChanged
        jTextFieldTambahAlatSkala.setText(Integer.toString(jSliderTambahAlatSkala.getValue()));
        refreshPeta(currImageIcon, alats);
    }//GEN-LAST:event_jSliderTambahAlatSkalaStateChanged

    private void jComboBoxUkuranTeksItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxUkuranTeksItemStateChanged
        if (evt.getID() == ItemEvent.ITEM_STATE_CHANGED) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                refreshPeta(currImageIcon, alats);
            }
        }
    }//GEN-LAST:event_jComboBoxUkuranTeksItemStateChanged

    private void jComboBoxHalamanKategoriAlatItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getID() == ItemEvent.ITEM_STATE_CHANGED) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {
                    currPageKategoriAlat = Integer.parseInt(jComboBoxHalamanKategoriAlat.getSelectedItem().toString());
                } catch (NumberFormatException e) {
                    currPageKategoriAlat = 0;
                }
                try {
                    refreshJTableKategoriAlat();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainFrame_Maintenance.this, "Gagal menyegarkan data Kategori Alat: " + ex.getLocalizedMessage(), "Penyegaran Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void jComboBoxUkuranPetaItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getID() == ItemEvent.ITEM_STATE_CHANGED) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                JComboBox<String> cb = (JComboBox<String>) evt.getSource();
                String newSelection = (String) cb.getSelectedItem();
                int percent;
                switch (newSelection) {
                    case "200%":
                        percent = 200;
                        jSliderUkuranPeta.removeChangeListener(jSliderUkuranPetaChangeListener);
                        jComboBoxUkuranPeta.removeItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.setValue(percent);
                        jComboBoxUkuranPeta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"200%", "175%", "150%", "125%", "100%", "75%", "50%", "25%"}));
                        jComboBoxUkuranPeta.setSelectedItem(newSelection);
                        jComboBoxUkuranPeta.addItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.addChangeListener(jSliderUkuranPetaChangeListener);
                        break;
                    case "175%":
                        percent = 175;
                        jSliderUkuranPeta.removeChangeListener(jSliderUkuranPetaChangeListener);
                        jComboBoxUkuranPeta.removeItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.setValue(percent);
                        jComboBoxUkuranPeta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"200%", "175%", "150%", "125%", "100%", "75%", "50%", "25%"}));
                        jComboBoxUkuranPeta.setSelectedItem(newSelection);
                        jComboBoxUkuranPeta.addItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.addChangeListener(jSliderUkuranPetaChangeListener);
                        break;
                    case "150%":
                        percent = 150;
                        jSliderUkuranPeta.removeChangeListener(jSliderUkuranPetaChangeListener);
                        jComboBoxUkuranPeta.removeItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.setValue(percent);
                        jComboBoxUkuranPeta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"200%", "175%", "150%", "125%", "100%", "75%", "50%", "25%"}));
                        jComboBoxUkuranPeta.setSelectedItem(newSelection);
                        jComboBoxUkuranPeta.addItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.addChangeListener(jSliderUkuranPetaChangeListener);
                        break;
                    case "125%":
                        percent = 125;
                        jSliderUkuranPeta.removeChangeListener(jSliderUkuranPetaChangeListener);
                        jComboBoxUkuranPeta.removeItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.setValue(percent);
                        jComboBoxUkuranPeta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"200%", "175%", "150%", "125%", "100%", "75%", "50%", "25%"}));
                        jComboBoxUkuranPeta.setSelectedItem(newSelection);
                        jComboBoxUkuranPeta.addItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.addChangeListener(jSliderUkuranPetaChangeListener);
                        break;
                    case "75%":
                        percent = 75;
                        jSliderUkuranPeta.removeChangeListener(jSliderUkuranPetaChangeListener);
                        jComboBoxUkuranPeta.removeItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.setValue(percent);
                        jComboBoxUkuranPeta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"200%", "175%", "150%", "125%", "100%", "75%", "50%", "25%"}));
                        jComboBoxUkuranPeta.setSelectedItem(newSelection);
                        jComboBoxUkuranPeta.addItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.addChangeListener(jSliderUkuranPetaChangeListener);
                        break;
                    case "50%":
                        percent = 50;
                        jSliderUkuranPeta.removeChangeListener(jSliderUkuranPetaChangeListener);
                        jComboBoxUkuranPeta.removeItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.setValue(percent);
                        jComboBoxUkuranPeta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"200%", "175%", "150%", "125%", "100%", "75%", "50%", "25%"}));
                        jComboBoxUkuranPeta.setSelectedItem(newSelection);
                        jComboBoxUkuranPeta.addItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.addChangeListener(jSliderUkuranPetaChangeListener);
                        break;
                    case "25%":
                        percent = 25;
                        jSliderUkuranPeta.removeChangeListener(jSliderUkuranPetaChangeListener);
                        jComboBoxUkuranPeta.removeItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.setValue(percent);
                        jComboBoxUkuranPeta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"200%", "175%", "150%", "125%", "100%", "75%", "50%", "25%"}));
                        jComboBoxUkuranPeta.setSelectedItem(newSelection);
                        jComboBoxUkuranPeta.addItemListener(jComboBoxUkuranPetaItemListener);
                        jSliderUkuranPeta.addChangeListener(jSliderUkuranPetaChangeListener);
                        break;
                    default:
                        percent = 100;
                        jSliderUkuranPeta.setValue(percent);
                }
                refreshAlat();
            }
        }
    }

    private void jSliderUkuranPetaStateChanged(javax.swing.event.ChangeEvent evt) {
        refreshPeta(currImageIcon, alats);
    }

    /**
     * @param args the command line arguments
     */
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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame_Maintenance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame_Maintenance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame_Maintenance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame_Maintenance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame_Maintenance().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonEksporExcelKategoriAlat;
    private javax.swing.JButton jButtonEksporExcelLantai;
    private javax.swing.JButton jButtonEksporExcelRuangan;
    private javax.swing.JButton jButtonEksporPDFKategoriAlat;
    private javax.swing.JButton jButtonEksporPDFLantai;
    private javax.swing.JButton jButtonEksporPDFRuangan;
    private javax.swing.JButton jButtonHapusTerpilihAlat;
    private javax.swing.JButton jButtonHapusTerpilihKategoriAlat;
    private javax.swing.JButton jButtonHapusTerpilihLantai;
    private javax.swing.JButton jButtonHapusTerpilihRuangan;
    private javax.swing.JButton jButtonJeda;
    private javax.swing.JButton jButtonKeluar;
    private javax.swing.JButton jButtonKurangUkuranPeta;
    private javax.swing.JButton jButtonPertamaKategoriAlat;
    private javax.swing.JButton jButtonPertamaLantai;
    private javax.swing.JButton jButtonPertamaRuangan;
    private javax.swing.JButton jButtonSebelumnyaKategoriAlat;
    private javax.swing.JButton jButtonSebelumnyaLantai;
    private javax.swing.JButton jButtonSebelumnyaLantai2;
    private javax.swing.JButton jButtonSebelumnyaRuangan;
    private javax.swing.JButton jButtonSegarkanAlat;
    private javax.swing.JButton jButtonSegarkanKategoriAlat;
    private javax.swing.JButton jButtonSegarkanLantai;
    private javax.swing.JButton jButtonSegarkanRuangan;
    private javax.swing.JButton jButtonSelanjutnyaKategoriAlat;
    private javax.swing.JButton jButtonSelanjutnyaLantai;
    private javax.swing.JButton jButtonSelanjutnyaLantai2;
    private javax.swing.JButton jButtonSelanjutnyaRuangan;
    private javax.swing.JButton jButtonTambahAlat;
    private javax.swing.JButton jButtonTambahAlatBatal;
    private javax.swing.JButton jButtonTambahAlatPilihBerkasFotoAlat;
    private javax.swing.JButton jButtonTambahAlatPosisiAtas;
    private javax.swing.JButton jButtonTambahAlatPosisiBawah;
    private javax.swing.JButton jButtonTambahAlatPosisiKanan;
    private javax.swing.JButton jButtonTambahAlatPosisiKiri;
    private javax.swing.JButton jButtonTambahAlatPosisiUlang;
    private javax.swing.JButton jButtonTambahAlatRotasi0;
    private javax.swing.JButton jButtonTambahAlatRotasi180;
    private javax.swing.JButton jButtonTambahAlatRotasi270;
    private javax.swing.JButton jButtonTambahAlatRotasi90;
    private javax.swing.JButton jButtonTambahAlatRotasiKanan;
    private javax.swing.JButton jButtonTambahAlatRotasiKiri;
    private javax.swing.JButton jButtonTambahAlatRotasiUlang;
    private javax.swing.JButton jButtonTambahAlatSimpan;
    private javax.swing.JButton jButtonTambahAlatSkala200;
    private javax.swing.JButton jButtonTambahAlatSkala50;
    private javax.swing.JButton jButtonTambahAlatSkalaUlang;
    private javax.swing.JButton jButtonTambahKategoriAlat;
    private javax.swing.JButton jButtonTambahLantai;
    private javax.swing.JButton jButtonTambahLantai2;
    private javax.swing.JButton jButtonTambahRuangan;
    private javax.swing.JButton jButtonTambahUkuranPeta;
    private javax.swing.JButton jButtonTerakhirKategoriAlat;
    private javax.swing.JButton jButtonTerakhirLantai;
    private javax.swing.JButton jButtonTerakhirRuangan;
    private javax.swing.JCheckBox jCheckBoxModaTV;
    private javax.swing.JCheckBox jCheckBoxPilihSemuaAlat;
    private javax.swing.JCheckBox jCheckBoxTampilkanNama;
    private javax.swing.JComboBox<String> jComboBoxHalamanKategoriAlat;
    private javax.swing.JComboBox<String> jComboBoxHalamanLantai;
    private javax.swing.JComboBox<String> jComboBoxHalamanRuangan;
    private javax.swing.JComboBox<String> jComboBoxLantai;
    private javax.swing.JComboBox<String> jComboBoxPerHalamanKategoriAlat;
    private javax.swing.JComboBox<String> jComboBoxPerHalamanLantai;
    private javax.swing.JComboBox<String> jComboBoxPerHalamanRuangan;
    private javax.swing.JComboBox<String> jComboBoxTambahAlatKategoriAlat;
    private javax.swing.JComboBox<String> jComboBoxTambahAlatRuangan;
    private javax.swing.JComboBox<String> jComboBoxUkuranPeta;
    private javax.swing.JComboBox<String> jComboBoxUkuranTeks;
    private javax.swing.JFileChooser jFileChooserTambahAlatFotoAlat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelCariAlat;
    private javax.swing.JLabel jLabelCariKategoriAlat;
    private javax.swing.JLabel jLabelCariLantai;
    private javax.swing.JLabel jLabelCariRuangan;
    private javax.swing.JLabel jLabelHalamanKategoriAlat;
    private javax.swing.JLabel jLabelHalamanLantai;
    private javax.swing.JLabel jLabelHalamanRuangan;
    private javax.swing.JLabel jLabelMasukSebagai;
    private javax.swing.JLabel jLabelPerHalamanKategoriAlat;
    private javax.swing.JLabel jLabelPerHalamanLantai;
    private javax.swing.JLabel jLabelPerHalamanRuangan;
    private javax.swing.JLabel jLabelPeta;
    private javax.swing.JLabel jLabelTambahAlatIDAlat;
    private javax.swing.JLabel jLabelTambahAlatKodeAlat;
    private javax.swing.JLabel jLabelTambahAlatPratinjauFotoAlat;
    private javax.swing.JLabel jLabelTerpilihAlat;
    private javax.swing.JLabel jLabelTerpilihKategoriAlat;
    private javax.swing.JLabel jLabelTerpilihLantai;
    private javax.swing.JLabel jLabelTerpilihRuangan;
    private javax.swing.JLabel jLabelWaktu;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanelDaftarAlat;
    private javax.swing.JPanel jPanelDataKategoriAlat;
    private javax.swing.JPanel jPanelDataLantai;
    private javax.swing.JPanel jPanelDataRuangan;
    private javax.swing.JPanel jPanelJadwalHariIni;
    private javax.swing.JPanel jPanelJadwalTerlambat;
    private javax.swing.JPanel jPanelKategoriAlat;
    private javax.swing.JPanel jPanelLantai;
    private javax.swing.JPanel jPanelLegendaPeta;
    private javax.swing.JPanel jPanelListDaftarAlat;
    private javax.swing.JPanel jPanelListDaftarJadwalHariIni;
    private javax.swing.JPanel jPanelListDaftarJadwalTerlambat;
    private javax.swing.JPanel jPanelPengaturanPeta;
    private javax.swing.JPanel jPanelPeta;
    private javax.swing.JPanel jPanelPetaMaintenance;
    private javax.swing.JPanel jPanelRuangan;
    private javax.swing.JPanel jPanelTambahAlat;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPaneKategoriAlat;
    private javax.swing.JScrollPane jScrollPaneLantai;
    private javax.swing.JScrollPane jScrollPanePeta;
    private javax.swing.JScrollPane jScrollPaneRuangan;
    private javax.swing.JScrollPane jScrollPaneTambahAlat;
    private javax.swing.JSeparator jSeparatorAturHalamanKategoriAlat;
    private javax.swing.JSeparator jSeparatorAturHalamanLantai;
    private javax.swing.JSeparator jSeparatorAturHalamanRuangan;
    private javax.swing.JSeparator jSeparatorHalamanKategoriAlat;
    private javax.swing.JSeparator jSeparatorHalamanLantai;
    private javax.swing.JSeparator jSeparatorHalamanRuangan;
    private javax.swing.JSeparator jSeparatorPerHalamanKategoriAlat;
    private javax.swing.JSeparator jSeparatorPerHalamanLantai;
    private javax.swing.JSeparator jSeparatorPerHalamanRuangan;
    private javax.swing.JSlider jSliderTambahAlatSkala;
    private javax.swing.JSlider jSliderUkuranPeta;
    private javax.swing.JTabbedPane jTabbedPaneWaletaMaintenance;
    private javax.swing.JTable jTableKategoriAlat;
    private javax.swing.JTable jTableLantai;
    private javax.swing.JTable jTableRuangan;
    private javax.swing.JTextArea jTextAreaTambahAlatSpesifikasi;
    private javax.swing.JTextField jTextFieldCariAlat;
    private javax.swing.JTextField jTextFieldCariKategoriAlat;
    private javax.swing.JTextField jTextFieldCariLantai;
    private javax.swing.JTextField jTextFieldCariRuangan;
    private javax.swing.JTextField jTextFieldTambahAlatFotoAlat;
    private javax.swing.JTextField jTextFieldTambahAlatIDAlat;
    private javax.swing.JTextField jTextFieldTambahAlatKodeAlat;
    private javax.swing.JTextField jTextFieldTambahAlatMerk;
    private javax.swing.JTextField jTextFieldTambahAlatPosX;
    private javax.swing.JTextField jTextFieldTambahAlatPosY;
    private javax.swing.JTextField jTextFieldTambahAlatRotasi;
    private javax.swing.JTextField jTextFieldTambahAlatSkala;
    private javax.swing.JToggleButton jToggleButtonHal10KategoriAlat;
    private javax.swing.JToggleButton jToggleButtonHal10Lantai;
    private javax.swing.JToggleButton jToggleButtonHal10Ruangan;
    private javax.swing.JToggleButton jToggleButtonHal11KategoriAlat;
    private javax.swing.JToggleButton jToggleButtonHal11Lantai;
    private javax.swing.JToggleButton jToggleButtonHal11Ruangan;
    private javax.swing.JToggleButton jToggleButtonHal1KategoriAlat;
    private javax.swing.JToggleButton jToggleButtonHal1Lantai;
    private javax.swing.JToggleButton jToggleButtonHal1Ruangan;
    private javax.swing.JToggleButton jToggleButtonHal2KategoriAlat;
    private javax.swing.JToggleButton jToggleButtonHal2Lantai;
    private javax.swing.JToggleButton jToggleButtonHal2Ruangan;
    private javax.swing.JToggleButton jToggleButtonHal3KategoriAlat;
    private javax.swing.JToggleButton jToggleButtonHal3Lantai;
    private javax.swing.JToggleButton jToggleButtonHal3Ruangan;
    private javax.swing.JToggleButton jToggleButtonHal4KategoriAlat;
    private javax.swing.JToggleButton jToggleButtonHal4Lantai;
    private javax.swing.JToggleButton jToggleButtonHal4Ruangan;
    private javax.swing.JToggleButton jToggleButtonHal5KategoriAlat;
    private javax.swing.JToggleButton jToggleButtonHal5Lantai;
    private javax.swing.JToggleButton jToggleButtonHal5Ruangan;
    private javax.swing.JToggleButton jToggleButtonHal6KategoriAlat;
    private javax.swing.JToggleButton jToggleButtonHal6Lantai;
    private javax.swing.JToggleButton jToggleButtonHal6Ruangan;
    private javax.swing.JToggleButton jToggleButtonHal7KategoriAlat;
    private javax.swing.JToggleButton jToggleButtonHal7Lantai;
    private javax.swing.JToggleButton jToggleButtonHal7Ruangan;
    private javax.swing.JToggleButton jToggleButtonHal8KategoriAlat;
    private javax.swing.JToggleButton jToggleButtonHal8Lantai;
    private javax.swing.JToggleButton jToggleButtonHal8Ruangan;
    private javax.swing.JToggleButton jToggleButtonHal9KategoriAlat;
    private javax.swing.JToggleButton jToggleButtonHal9Lantai;
    private javax.swing.JToggleButton jToggleButtonHal9Ruangan;
    private javax.swing.JToggleButton jToggleButtonPerbesarSemuaAlat;
    // End of variables declaration//GEN-END:variables
}
