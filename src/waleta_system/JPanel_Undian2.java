package waleta_system;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import waleta_system.Class.Utility;

public class JPanel_Undian2 extends javax.swing.JPanel {

    private JLabel lblBg;
    private JLabel[] arrLblNo, arrLblNama, arrLblBagian, arrLblAnimasi;
    private List<MPeserta>[] arrPesertaKloter;
    private Timer t = null;
    private int[] arrUrutanKloterRandom;
    private int kloter = 0, angkaRandom, angkaRandomPemenang = 0, maxPemenang, page = 0, maxPage = 0;
    private boolean cekTimerRun = false;
    private List<MPeserta> arrSemuaPeserta, listPemenang;
    private MPeserta pemenang;
    private String judul, subjudul;
    private Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
    private Thread tBgMusic, tSoundEffect;
    private SoundJLayer soundBgMusic, soundEffect;

    public JPanel_Undian2() {
        initComponents();
        lblBg = new JLabel();
        lblBg.setIcon(new ImageIcon(new ImageIcon(Objects.requireNonNull(this.getClass().getResource("Images/bg_undian_karyawan_disiplin.jpg"))).getImage().getScaledInstance((int) size.getWidth(), (int) size.getHeight(), Image.SCALE_DEFAULT)));
        lblBg.setBounds(0, 0, (int) size.getWidth(), (int) size.getHeight());
        lblBg.setVisible(false);
        pnlBg.add(lblBg);
        invalidate();
        repaint();
        btnMulaiUndian.setVisible(false);
        lblJudul.setVisible(false);
        lblSubJudul.setVisible(false);
        lblSelamat.setVisible(false);
        lblReset.setVisible(false);
        btnPrev.setVisible(false);
        btnSimpan.setVisible(false);
        btnNext.setVisible(false);

        fcExcel.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "Excel File (*.xls, *.xlsx)";
            }

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String filename = f.getName().toLowerCase();
                    return filename.endsWith(".xls") || filename.endsWith(".xlsx");
                }
            }
        });
    }

    private void undian() {
        if (lblBg.isVisible()) {
            if (!arrLblAnimasi[0].isVisible()) {
                if (!cekTimerRun) {
                    if (kloter < maxPemenang) {
                        if (tSoundEffect != null) {
                            soundEffect.close();
                            tSoundEffect.stop();
                            tSoundEffect = null;
                        }
                        tSoundEffect = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                soundEffect = new SoundJLayer("Report\\Sound\\undian_rolling.mp3", true);
                                soundEffect.play();
                            }
                        });
                        tSoundEffect.start();
                        if (kloter % 4 == 0) {
                            for (int i = 0; i < arrLblNo.length; i++) {
                                arrLblNo[i].setVisible(false);
                                arrLblNama[i].setVisible(false);
                                arrLblBagian[i].setVisible(false);
                            }
                        }
                        kloter++;
                        arrLblNo[(kloter - 1) % 4].setText(kloter + "");
                        arrLblNo[(kloter - 1) % 4].setVisible(true);
                        arrLblNama[(kloter - 1) % 4].setVisible(true);
                        arrLblBagian[(kloter - 1) % 4].setVisible(true);
                        t = new Timer();
                        t.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                int angkaRandomNew = -1;
                                do {
                                    angkaRandom = angkaRandomNew;
                                    angkaRandomNew = new Random().nextInt(arrSemuaPeserta.size());
                                } while (angkaRandom == angkaRandomNew);
                                angkaRandom = angkaRandomNew;
                                arrLblNama[(kloter - 1) % 4].setText(arrSemuaPeserta.get(angkaRandom).getNama());
                                arrLblBagian[(kloter - 1) % 4].setText(arrSemuaPeserta.get(angkaRandom).getBagian());
                            }
                        }, 0, 50);
                        cekTimerRun = true;
                    }
                } else {
                    if (tSoundEffect != null) {
                        soundEffect.close();
                        tSoundEffect.stop();
                        tSoundEffect = null;
                    }
                    tSoundEffect = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            soundEffect = new SoundJLayer("Report\\Sound\\undian_win.mp3", true);
                            soundEffect.play();
                        }
                    });
                    tSoundEffect.start();
                    lblReset.setText("ULANG");
                    lblReset.setVisible(true);
                    angkaRandomPemenang = new Random().nextInt(arrPesertaKloter[arrUrutanKloterRandom[kloter - 1]].size());
                    pemenang = arrPesertaKloter[arrUrutanKloterRandom[kloter - 1]].get(angkaRandomPemenang);
                    arrLblNama[(kloter - 1) % 4].setText(arrPesertaKloter[arrUrutanKloterRandom[kloter - 1]].get(angkaRandomPemenang).getNama());
                    arrLblBagian[(kloter - 1) % 4].setText(arrPesertaKloter[arrUrutanKloterRandom[kloter - 1]].get(angkaRandomPemenang).getBagian());
                    arrLblNo[(kloter - 1) % 4].setForeground(Color.BLUE);
                    arrLblNama[(kloter - 1) % 4].setForeground(Color.BLUE);
                    arrLblBagian[(kloter - 1) % 4].setForeground(Color.BLUE);
                    for (int i = 0; i < arrLblAnimasi.length; i++) {
                        arrLblAnimasi[i].setVisible(true);
                    }
                    t.cancel();
                    cekTimerRun = false;
                }
            } else {
                if (tSoundEffect != null) {
                    soundEffect.close();
                    tSoundEffect.stop();
                    tSoundEffect = null;
                }
                listPemenang.add(pemenang);
                arrPesertaKloter[arrUrutanKloterRandom[kloter - 1]].remove(angkaRandomPemenang);
                lblReset.setVisible(false);
                if (kloter == maxPemenang) {
                    lblSelamat.setVisible(true);
                    lblReset.setText("RESET");
                    lblReset.setVisible(true);
                    btnSimpan.setVisible(true);
                    btnSimpan.setEnabled(true);
                    if (maxPemenang > 4) {
                        btnPrev.setVisible(true);
                        btnPrev.setEnabled(true);
                        btnNext.setVisible(true);
                        btnNext.setEnabled(false);
                    }
                }
                for (int i = 0; i < arrLblAnimasi.length; i++) {
                    arrLblAnimasi[i].setVisible(false);
                }
            }
        }
    }

    private void insertUndian() {
        try {
            Utility.db.getConnection().setAutoCommit(false);
            String insert_undian = "INSERT INTO tb_undian(tgl_undian, nama_undian, id_pegawai) "
                    + "VALUES (CURRENT_TIMESTAMP(),"
                    + "'" + judul + (subjudul.equals("") ? "" : "\n" + subjudul) + "',"
                    + "'" + MainForm.Login_idPegawai + "')";
            if ((Utility.db.getStatement().executeUpdate(insert_undian)) == 1) {
                String get_last_id_undian = "SELECT id_undian FROM tb_undian ORDER BY id_undian DESC LIMIT 1";
                ResultSet result = Utility.db.getStatement().executeQuery(get_last_id_undian);
                if (result.next()) {
                    int idUndian = result.getInt("id_undian");
                    boolean cek = true;
                    for (int i = 0; i < listPemenang.size(); i++) {
                        String insert_undian_detail = "INSERT INTO tb_undian_detail(id_undian, nama, bagian) "
                                + "VALUES (" + idUndian + ","
                                + "'" + listPemenang.get(i).getNama() + "',"
                                + "'" + listPemenang.get(i).getBagian() + "')";
                        if ((Utility.db.getStatement().executeUpdate(insert_undian_detail)) != 1) {
                            Utility.db.getConnection().rollback();
                            JOptionPane.showMessageDialog(this, "Save data pemenang undian failed!");
                            break;
                        }
                    }
                    if (cek) {
                        Utility.db.getConnection().commit();
                        btnSimpan.setEnabled(false);
                        JOptionPane.showMessageDialog(this, "Save data pemenang undian berhasil !");
                    }
                } else {
                    Utility.db.getConnection().rollback();
                    JOptionPane.showMessageDialog(this, "Save data pemenang undian failed!");
                }
            } else {
                Utility.db.getConnection().rollback();
                JOptionPane.showMessageDialog(this, "Save data pemenang undian failed!");
            }
        } catch (Exception e) {
            try {
                Utility.db.getConnection().rollback();
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_Undian2.class.getName()).log(Level.SEVERE, null, ex);
            }
            JOptionPane.showMessageDialog(this, "FAILED : " + e);
            Logger.getLogger(JPanel_Undian2.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                Utility.db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(JPanel_Undian2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public class MPeserta {

        private String nama;
        private String bagian;

        public MPeserta(String nama, String bagian) {
            this.nama = nama;
            this.bagian = bagian;
        }

        public MPeserta() {

        }

        public String getNama() {
            return nama;
        }

        public void setNama(String nama) {
            this.nama = nama;
        }

        public String getBagian() {
            return bagian;
        }

        public void setBagian(String bagian) {
            this.bagian = bagian;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fcExcel = new javax.swing.JFileChooser();
        pnlBg = new javax.swing.JPanel();
        btnMulaiUndian = new javax.swing.JButton();
        btnBrowse = new javax.swing.JButton();
        lblPilihFileExcel = new javax.swing.JLabel();
        lblJudul = new javax.swing.JLabel();
        lblSubJudul = new javax.swing.JLabel();
        lblSelamat = new javax.swing.JLabel();
        lblReset = new javax.swing.JLabel();
        lblSpace = new javax.swing.JLabel();
        btnPrev = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        lblSpace1 = new javax.swing.JLabel();
        lblSpace2 = new javax.swing.JLabel();

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        pnlBg.setBackground(new java.awt.Color(255, 255, 255));

        btnMulaiUndian.setBackground(new java.awt.Color(255, 255, 255));
        btnMulaiUndian.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnMulaiUndian.setText("Mulai Undian");
        btnMulaiUndian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMulaiUndianActionPerformed(evt);
            }
        });

        btnBrowse.setBackground(new java.awt.Color(255, 255, 255));
        btnBrowse.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        btnBrowse.setText("Browse");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        lblPilihFileExcel.setBackground(new java.awt.Color(255, 255, 255));
        lblPilihFileExcel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblPilihFileExcel.setText("Silahkan pilih file Excel");

        lblJudul.setFont(new java.awt.Font("Trebuchet MS", 1, 65)); // NOI18N
        lblJudul.setForeground(new java.awt.Color(156, 30, 39));
        lblJudul.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblJudul.setText("PEMENANG KARYAWAN DISIPLIN WALETA");

        lblSubJudul.setFont(new java.awt.Font("Trebuchet MS", 1, 48)); // NOI18N
        lblSubJudul.setForeground(new java.awt.Color(156, 30, 39));
        lblSubJudul.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSubJudul.setText("Periode");

        lblSelamat.setFont(new java.awt.Font("Trebuchet MS", 1, 60)); // NOI18N
        lblSelamat.setForeground(new java.awt.Color(156, 30, 39));
        lblSelamat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSelamat.setText("Selamat kepada para pemenang ...");

        lblReset.setBackground(new java.awt.Color(255, 255, 255));
        lblReset.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblReset.setText("RESET");
        lblReset.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblResetMouseClicked(evt);
            }
        });

        lblSpace.setBackground(new java.awt.Color(255, 255, 255));
        lblSpace.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblSpace.setText("  ");

        btnPrev.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnPrev.setText("<");
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });

        btnSimpan.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSimpan.setText("SIMPAN");
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });

        btnNext.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnNext.setText(">");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        lblSpace1.setBackground(new java.awt.Color(255, 255, 255));
        lblSpace1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblSpace1.setText("  ");

        lblSpace2.setBackground(new java.awt.Color(255, 255, 255));
        lblSpace2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblSpace2.setText("  ");

        javax.swing.GroupLayout pnlBgLayout = new javax.swing.GroupLayout(pnlBg);
        pnlBg.setLayout(pnlBgLayout);
        pnlBgLayout.setHorizontalGroup(
            pnlBgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBgLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSelamat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblJudul, javax.swing.GroupLayout.DEFAULT_SIZE, 1520, Short.MAX_VALUE)
                    .addComponent(lblSubJudul, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlBgLayout.createSequentialGroup()
                        .addComponent(lblSpace2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnPrev)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnNext)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblSpace1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlBgLayout.createSequentialGroup()
                        .addComponent(lblPilihFileExcel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBrowse)
                        .addGap(9, 9, 9)
                        .addComponent(btnMulaiUndian)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblReset)
                        .addGap(3, 3, 3)
                        .addComponent(lblSpace)))
                .addContainerGap())
        );
        pnlBgLayout.setVerticalGroup(
            pnlBgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBgLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPilihFileExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnMulaiUndian, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlBgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblReset)
                        .addComponent(lblSpace)))
                .addGap(143, 143, 143)
                .addComponent(lblJudul, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubJudul)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 490, Short.MAX_VALUE)
                .addGroup(pnlBgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPrev)
                    .addComponent(btnNext)
                    .addComponent(lblSpace1)
                    .addComponent(lblSpace2)
                    .addComponent(btnSimpan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSelamat)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlBg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlBg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnMulaiUndianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMulaiUndianActionPerformed
        if (arrPesertaKloter == null) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih file Excel terlebih dahulu !");
        } else {
            listPemenang = new ArrayList<>();
            lblPilihFileExcel.setVisible(false);
            btnBrowse.setVisible(false);
            btnMulaiUndian.setVisible(false);
            lblBg.setVisible(true);
            lblJudul.setText(judul);
            lblSubJudul.setText(subjudul);
            lblJudul.setVisible(true);
            lblSubJudul.setVisible(true);
            int maxPesertaKloter = maxPemenang;
            if (maxPesertaKloter > 4) {
                maxPesertaKloter = 4;
            }
            maxPage = (maxPemenang - 1) / 4;
            page = maxPage;
            arrLblNo = new JLabel[maxPesertaKloter];
            arrLblNama = new JLabel[maxPesertaKloter];
            arrLblBagian = new JLabel[maxPesertaKloter];
            int margin = 110;
            if (maxPesertaKloter > 6) {
                margin -= (maxPesertaKloter - 6) * 10;
            }
            for (int i = 0; i < maxPesertaKloter; i++) {
                arrLblNo[i] = new JLabel("");
                arrLblNo[i].setBounds(200, 370 + (i * margin), 953, 62);
                arrLblNo[i].setFont(new Font("Trebuchet MS", 0, 60));
                arrLblNo[i].setVisible(false);
                pnlBg.add(arrLblNo[i], 0);

                arrLblNama[i] = new JLabel("");
                arrLblNama[i].setBounds(300, 370 + (i * margin), 850, 71);
                arrLblNama[i].setFont(new Font("Trebuchet MS", 0, 60));
                arrLblNama[i].setVisible(false);
                pnlBg.add(arrLblNama[i], 0);

                arrLblBagian[i] = new JLabel("");
                arrLblBagian[i].setBounds(1200, 370 + (i * margin), 600, 71);
                arrLblBagian[i].setFont(new Font("Trebuchet MS", 0, 60));
                arrLblBagian[i].setVisible(false);
                pnlBg.add(arrLblBagian[i], 0);
            }
            ImageIcon iiGif = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("Images/confetti.gif")));
            int wMultiply = (int) Math.ceil(size.getWidth() / iiGif.getIconWidth());
            int hMultiply = (int) Math.ceil(size.getHeight() / iiGif.getIconHeight());
            arrLblAnimasi = new JLabel[wMultiply * hMultiply];
            int idx = -1;
            for (int i = 0; i < hMultiply; i++) {
                for (int j = 0; j < wMultiply; j++) {
                    idx++;
                    arrLblAnimasi[idx] = new JLabel();
                    arrLblAnimasi[idx].setIcon(iiGif);
                    arrLblAnimasi[idx].setBounds(iiGif.getIconWidth() * j, iiGif.getIconHeight() * i, (int) size.getWidth(), (int) size.getHeight());
                    arrLblAnimasi[idx].setVisible(false);
                    pnlBg.add(arrLblAnimasi[idx], 0);
                }
            }
            tBgMusic = new Thread(new Runnable() {
                @Override
                public void run() {
                    soundBgMusic = new SoundJLayer("Report\\Sound\\undian_bg_music.mp3", true);
                    soundBgMusic.play();
                }
            });
            tBgMusic.start();
        }
    }//GEN-LAST:event_btnMulaiUndianActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        int fcExcelVal = fcExcel.showOpenDialog(this);
        if (fcExcelVal == fcExcel.APPROVE_OPTION) {
            try {
                File file = fcExcel.getSelectedFile();
                FileInputStream fis = new FileInputStream(file);
                XSSFWorkbook wb = new XSSFWorkbook(fis);
                XSSFSheet sheet = wb.getSheetAt(0);
                Iterator<Row> itr = sheet.iterator();
                int maxKloterData = 0;
                int rowNo = 1;
                while (itr.hasNext()) {
                    Row row = itr.next();
                    if (rowNo == 1) {
                        row = itr.next();
                        rowNo++;
                    }
                    if (rowNo == 2) {
                        row = itr.next();
                        rowNo++;
                    }
                    int colNo = 1;
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        XSSFCell cell = (XSSFCell) cellIterator.next();
                        if (colNo == 3) {
                            if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                                if (cell.getNumericCellValue() > maxKloterData) {
                                    maxKloterData = (int) cell.getNumericCellValue();
                                }
                            } else if (cell.getCellTypeEnum() == CellType.FORMULA) {
                                if (cell.getCachedFormulaResultTypeEnum() == CellType.NUMERIC) {
                                    maxKloterData = (int) cell.getNumericCellValue();
                                }
                            }
                        }
                        colNo++;
                    }
                    rowNo++;
                }
                arrSemuaPeserta = new ArrayList<>();
                arrPesertaKloter = (List<MPeserta>[]) new List[maxKloterData];
                itr = sheet.iterator();
                rowNo = 1;
                int minPrioritas = 0, maxPrioritas = -1, minNoPrioritas = -1, maxNoPrioritas = maxKloterData - 1;
                int[] arrJmlPemenang = new int[maxKloterData];
                while (itr.hasNext()) {
                    Row row = itr.next();
                    if (rowNo == 1) {
                        Iterator<Cell> cellIterator = row.cellIterator();
                        if (cellIterator.hasNext()) {
                            XSSFCell cell = (XSSFCell) cellIterator.next();
                            String[] judulTemp = cell.getStringCellValue().split("###");
                            judul = judulTemp[0];
                            if (judulTemp.length > 1) {
                                subjudul = judulTemp[1];
                            }
                        }
                        row = itr.next();
                        rowNo++;
                    }
                    if (rowNo == 2) {
                        row = itr.next();
                        rowNo++;
                    }
                    int colNo = 1;
                    Iterator<Cell> cellIterator = row.cellIterator();
                    MPeserta mPeserta = new MPeserta();
                    int kloter = -1;
                    boolean peserta = false;
                    while (cellIterator.hasNext()) {
                        XSSFCell cell = (XSSFCell) cellIterator.next();
                        if (colNo == 1) {
                            if (cell.getCellTypeEnum() == CellType.STRING) {
                                mPeserta.setNama(cell.getStringCellValue());
                            } else if (cell.getCellTypeEnum() == CellType.FORMULA) {
                                if (cell.getCachedFormulaResultTypeEnum() == CellType.STRING) {
                                    mPeserta.setNama(cell.getStringCellValue());
                                }
                            }
                        } else if (colNo == 2) {
                            if (cell.getCellTypeEnum() == CellType.STRING) {
                                mPeserta.setBagian(cell.getStringCellValue());
                            } else if (cell.getCellTypeEnum() == CellType.FORMULA) {
                                if (cell.getCachedFormulaResultTypeEnum() == CellType.STRING) {
                                    mPeserta.setBagian(cell.getStringCellValue());
                                }
                            }
                        } else if (colNo == 3) {
                            if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                                kloter = (int) cell.getNumericCellValue();
                                if (kloter > 0) {
                                    peserta = true;
                                }
                            } else if (cell.getCellTypeEnum() == CellType.FORMULA) {
                                if (cell.getCachedFormulaResultTypeEnum() == CellType.NUMERIC) {
                                    kloter = (int) cell.getNumericCellValue();
                                    if (kloter > 0) {
                                        peserta = true;
                                    }
                                }
                            }
                        } else if (colNo == 4) {
                            if (kloter > 0) {
                                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                                    int prioritas = (int) cell.getNumericCellValue();
                                    if (prioritas == 0 && maxPrioritas == -1) {
                                        maxPrioritas = kloter - 2;
                                        minNoPrioritas = kloter - 1;
                                    }
                                } else if (cell.getCellTypeEnum() == CellType.FORMULA) {
                                    if (cell.getCachedFormulaResultTypeEnum() == CellType.NUMERIC) {
                                        int prioritas = (int) cell.getNumericCellValue();
                                        if (prioritas == 0 && maxPrioritas == -1) {
                                            maxPrioritas = kloter - 2;
                                            minNoPrioritas = kloter - 1;
                                        }
                                    }
                                }
                            }
                        } else if (colNo == 5) {
                            if (kloter > 0) {
                                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                                    arrJmlPemenang[kloter - 1] = (int) cell.getNumericCellValue();
                                } else if (cell.getCellTypeEnum() == CellType.FORMULA) {
                                    if (cell.getCachedFormulaResultTypeEnum() == CellType.NUMERIC) {
                                        arrJmlPemenang[kloter - 1] = (int) cell.getNumericCellValue();
                                    }
                                }
                            }
                        }
                        colNo++;
                    }
                    arrSemuaPeserta.add(mPeserta);
                    if (peserta) {
                        if (arrPesertaKloter[kloter - 1] == null) {
                            arrPesertaKloter[kloter - 1] = new ArrayList<>();
                        }
                        arrPesertaKloter[kloter - 1].add(mPeserta);
                    }
                    rowNo++;
                }
                maxPemenang = 0;
                for (int i = 0; i <= minNoPrioritas; i++) {
                    maxPemenang += arrJmlPemenang[i];
                }
                arrUrutanKloterRandom = new int[maxPemenang];
                List<Integer> listNo = new ArrayList<>();
                List<Integer> listNo_NoPrioritas = new ArrayList<>();
                for (int i = 0; i <= minNoPrioritas; i++) {
                    listNo.add(i);
                }
                int no = 0;
                int[] jmlPesertaNoPrioritas = new int[maxKloterData];
                for (int i = minNoPrioritas; i <= maxNoPrioritas; i++) {
                    listNo_NoPrioritas.add(i);
                    jmlPesertaNoPrioritas[i] = arrPesertaKloter[i].size();
                }
                do {
                    int angkaRandom = new Random().nextInt(listNo.size());
                    if (listNo.get(angkaRandom) >= minPrioritas && listNo.get(angkaRandom) <= maxPrioritas) {
                        arrUrutanKloterRandom[no++] = listNo.get(angkaRandom);
                        arrJmlPemenang[listNo.get(angkaRandom)]--;
                        if (arrJmlPemenang[listNo.get(angkaRandom)] == 0) {
                            listNo.remove(angkaRandom);
                        }
                    } else {
                        if (listNo_NoPrioritas.size() > 0) {
                            int angkaRandom2 = new Random().nextInt(listNo_NoPrioritas.size());
                            arrUrutanKloterRandom[no++] = listNo_NoPrioritas.get(angkaRandom2);
                            jmlPesertaNoPrioritas[listNo_NoPrioritas.get(angkaRandom2)]--;
                            listNo_NoPrioritas.remove(angkaRandom2);
                            arrJmlPemenang[listNo.get(angkaRandom)]--;
                            if (arrJmlPemenang[listNo.get(angkaRandom)] == 0) {
                                listNo.remove(angkaRandom);
                            }
                        } else {
                            List<Integer> listNoTemp = new ArrayList<>();
                            for (int i = minNoPrioritas; i <= maxNoPrioritas; i++) {
                                if (jmlPesertaNoPrioritas[i] > 0) {
                                    listNoTemp.add(i);
                                }
                            }
                            int angkaRandom2 = new Random().nextInt(listNoTemp.size());
                            arrUrutanKloterRandom[no++] = listNoTemp.get(angkaRandom2);
                            jmlPesertaNoPrioritas[listNoTemp.get(angkaRandom2)]--;
                            arrJmlPemenang[listNo.get(angkaRandom)]--;
                            if (arrJmlPemenang[listNo.get(angkaRandom)] == 0) {
                                listNo.remove(angkaRandom);
                            }
                        }
                    }
                } while (listNo.size() > 0);
                btnMulaiUndian.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        undian();
    }//GEN-LAST:event_formMouseClicked

    private void lblResetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblResetMouseClicked
        if (lblReset.getText().equals("RESET")) {
            if (tBgMusic != null) {
                soundBgMusic.close();
                tBgMusic.stop();
                tBgMusic = null;
            }
            lblPilihFileExcel.setVisible(true);
            btnBrowse.setVisible(true);
            btnMulaiUndian.setVisible(false);
            lblBg.setVisible(false);
            lblJudul.setVisible(false);
            lblSubJudul.setVisible(false);
            lblSelamat.setVisible(false);
            lblReset.setVisible(false);
            btnPrev.setVisible(false);
            btnSimpan.setVisible(false);
            btnNext.setVisible(false);
            for (int i = 0; i < arrLblNo.length; i++) {
                arrLblNo[i].setVisible(false);
                arrLblNama[i].setVisible(false);
                arrLblBagian[i].setVisible(false);
            }
            arrLblNo = null;
            arrLblNama = null;
            arrLblBagian = null;
            arrPesertaKloter = null;
            cekTimerRun = false;
            t = null;
            kloter = 0;
        } else if (lblReset.getText().equals("ULANG")) {
            for (int i = 0; i < arrLblAnimasi.length; i++) {
                arrLblAnimasi[i].setVisible(false);
            }
            arrLblNo[(kloter - 1) % 4].setForeground(Color.BLACK);
            arrLblNama[(kloter - 1) % 4].setForeground(Color.BLACK);
            arrLblBagian[(kloter - 1) % 4].setForeground(Color.BLACK);
            kloter--;
            lblReset.setVisible(false);
            undian();
        }
    }//GEN-LAST:event_lblResetMouseClicked

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        page--;
        for (int i = 0; i < arrLblNo.length; i++) {
            arrLblNo[i].setVisible(true);
            arrLblNama[i].setVisible(true);
            arrLblBagian[i].setVisible(true);
            arrLblNo[i].setText(4 * page + i + 1 + "");
            arrLblNama[i].setText(listPemenang.get(4 * page + i).getNama());
            arrLblBagian[i].setText(listPemenang.get(4 * page + i).getBagian());
        }
        if (page == 0) {
            btnPrev.setEnabled(false);
        } else {
            btnPrev.setEnabled(true);
        }
        btnNext.setEnabled(true);
    }//GEN-LAST:event_btnPrevActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        page++;
        for (int i = 0; i < arrLblNo.length; i++) {
            arrLblNo[i].setVisible(false);
            arrLblNama[i].setVisible(false);
            arrLblBagian[i].setVisible(false);
        }
        int jml = ((maxPemenang % 4) == 0 ? 4 : (maxPemenang % 4));
        for (int i = 0; i < jml; i++) {
            arrLblNo[i].setVisible(true);
            arrLblNama[i].setVisible(true);
            arrLblBagian[i].setVisible(true);
            arrLblNo[i].setText(4 * page + i + 1 + "");
            arrLblNama[i].setText(listPemenang.get(4 * page + i).getNama());
            arrLblBagian[i].setText(listPemenang.get(4 * page + i).getBagian());
        }
        if (page == maxPage) {
            btnNext.setEnabled(false);
        } else {
            btnNext.setEnabled(true);
        }
        btnPrev.setEnabled(true);
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        insertUndian();
    }//GEN-LAST:event_btnSimpanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnMulaiUndian;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JFileChooser fcExcel;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblPilihFileExcel;
    private javax.swing.JLabel lblReset;
    private javax.swing.JLabel lblSelamat;
    private javax.swing.JLabel lblSpace;
    private javax.swing.JLabel lblSpace1;
    private javax.swing.JLabel lblSpace2;
    private javax.swing.JLabel lblSubJudul;
    private javax.swing.JPanel pnlBg;
    // End of variables declaration//GEN-END:variables
}
