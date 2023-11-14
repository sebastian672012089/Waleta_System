package waleta_system;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
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

public class JPanel_Undian extends javax.swing.JPanel {

    private JLabel lblBg;
    private JLabel[] arrLblNo, arrLblNama, arrLblBagian, arrLblAnimasi;
    private List<MPeserta>[] arrPesertaKloter;
    private Timer t = null;
    private int[] arrUrutanKloterRandom;
    private int kloter = 0, angkaRandom;
    private boolean cekTimerRun = false;
    private List<MPeserta> arrSemuaPeserta;
    private String judul;
    private Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

    public JPanel_Undian() {
        initComponents();
        lblBg = new JLabel();
        lblBg.setIcon(new ImageIcon(new ImageIcon(Objects.requireNonNull(this.getClass().getResource("Images/bg_undian_karyawan_disiplin.jpg"))).getImage().getScaledInstance((int) size.getWidth(), (int) size.getHeight(), Image.SCALE_DEFAULT)));
        lblBg.setBounds(0, 0, (int) size.getWidth(), (int) size.getHeight());
        lblBg.setVisible(false);
        pnlBg.add(lblBg);
        invalidate();
        repaint();
        btnMulaiUndian.setVisible(false);
        Lbl_judul.setVisible(false);
        lblSelamat.setVisible(false);
        LblReset.setVisible(false);

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
                    if (kloter < arrPesertaKloter.length) {
                        if (kloter % 10 == 0) {
                            for (int i = 0; i < arrLblNo.length; i++) {
                                arrLblNo[i].setVisible(false);
                                arrLblNama[i].setVisible(false);
                                arrLblBagian[i].setVisible(false);
                            }
                        }
                        kloter++;
                        arrLblNo[(kloter - 1) % 10].setText(kloter + "");
                        arrLblNo[(kloter - 1) % 10].setVisible(true);
                        arrLblNama[(kloter - 1) % 10].setVisible(true);
                        arrLblBagian[(kloter - 1) % 10].setVisible(true);
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
                                arrLblNama[(kloter - 1) % 10].setText(arrSemuaPeserta.get(angkaRandom).getNama());
                                arrLblBagian[(kloter - 1) % 10].setText(arrSemuaPeserta.get(angkaRandom).getBagian());
                            }
                        }, 0, 50);
                        cekTimerRun = true;
                    }
                } else {
                    LblReset.setText("ULANG");
                    LblReset.setVisible(true);
                    int angkaRandom = new Random().nextInt(arrPesertaKloter[arrUrutanKloterRandom[kloter - 1]].size());
                    arrLblNama[(kloter - 1) % 10].setText(arrPesertaKloter[arrUrutanKloterRandom[kloter - 1]].get(angkaRandom).getNama());
                    arrLblBagian[(kloter - 1) % 10].setText(arrPesertaKloter[arrUrutanKloterRandom[kloter - 1]].get(angkaRandom).getBagian());
                    arrLblNo[(kloter - 1) % 10].setForeground(Color.BLUE);
                    arrLblNama[(kloter - 1) % 10].setForeground(Color.BLUE);
                    arrLblBagian[(kloter - 1) % 10].setForeground(Color.BLUE);
                    for (int i = 0; i < arrLblAnimasi.length; i++) {
                        arrLblAnimasi[i].setVisible(true);
                    }
                    t.cancel();
                    cekTimerRun = false;
                }
            } else {
                LblReset.setVisible(false);
                if (kloter == arrPesertaKloter.length) {
                    lblSelamat.setVisible(true);
                    LblReset.setText("RESET");
                    LblReset.setVisible(true);
                }
                for (int i = 0; i < arrLblAnimasi.length; i++) {
                    arrLblAnimasi[i].setVisible(false);
                }
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fcExcel = new javax.swing.JFileChooser();
        pnlBg = new javax.swing.JPanel();
        btnMulaiUndian = new javax.swing.JButton();
        btnBrowse = new javax.swing.JButton();
        lblPilihFileExcel = new javax.swing.JLabel();
        Lbl_judul = new javax.swing.JLabel();
        lblSelamat = new javax.swing.JLabel();
        LblReset = new javax.swing.JLabel();

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

        Lbl_judul.setFont(new java.awt.Font("Trebuchet MS", 1, 65)); // NOI18N
        Lbl_judul.setForeground(new java.awt.Color(156, 30, 39));
        Lbl_judul.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Lbl_judul.setText("PEMENANG KARYAWAN DISIPLIN WALETA");

        lblSelamat.setFont(new java.awt.Font("Trebuchet MS", 1, 60)); // NOI18N
        lblSelamat.setForeground(new java.awt.Color(156, 30, 39));
        lblSelamat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSelamat.setText("Selamat kepada para pemenang ...");

        LblReset.setBackground(new java.awt.Color(255, 255, 255));
        LblReset.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        LblReset.setText("RESET");
        LblReset.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LblResetMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnlBgLayout = new javax.swing.GroupLayout(pnlBg);
        pnlBg.setLayout(pnlBgLayout);
        pnlBgLayout.setHorizontalGroup(
            pnlBgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBgLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBgLayout.createSequentialGroup()
                        .addComponent(lblPilihFileExcel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBrowse)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMulaiUndian)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(LblReset)
                        .addGap(15, 15, 15))
                    .addComponent(lblSelamat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Lbl_judul, javax.swing.GroupLayout.DEFAULT_SIZE, 1520, Short.MAX_VALUE))
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
                    .addComponent(LblReset))
                .addGap(148, 148, 148)
                .addComponent(Lbl_judul, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 603, Short.MAX_VALUE)
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
            lblPilihFileExcel.setVisible(false);
            btnBrowse.setVisible(false);
            btnMulaiUndian.setVisible(false);
            lblBg.setVisible(true);
            Lbl_judul.setText(judul);
            Lbl_judul.setVisible(true);
            arrUrutanKloterRandom = new int[arrPesertaKloter.length];
            List<Integer> listNo = new ArrayList<>();
            for (int i = 0; i < arrPesertaKloter.length; i++) {
                listNo.add(i);
            }
            int no = 0;
            do {
                int angkaRandom = new Random().nextInt(listNo.size());
                arrUrutanKloterRandom[no++] = listNo.get(angkaRandom);
                listNo.remove(angkaRandom);
            } while (listNo.size() > 0);
            int maxPesertaKloter = arrPesertaKloter.length;
            if (maxPesertaKloter > 10) {
                maxPesertaKloter = 10;
            }
            arrLblNo = new JLabel[maxPesertaKloter];
            arrLblNama = new JLabel[maxPesertaKloter];
            arrLblBagian = new JLabel[maxPesertaKloter];
            int margin = 110;
            if (maxPesertaKloter > 6) {
                margin -= (maxPesertaKloter - 6) * 10;
            }
            for (int i = 0; i < maxPesertaKloter; i++) {
                arrLblNo[i] = new JLabel("");
                arrLblNo[i].setBounds(200, 270 + (i * margin), 953, 62);
                arrLblNo[i].setFont(new Font("Trebuchet MS", 0, 60));
                arrLblNo[i].setVisible(false);
                pnlBg.add(arrLblNo[i], 0);

                arrLblNama[i] = new JLabel("");
                arrLblNama[i].setBounds(300, 270 + (i * margin), 953, 71);
                arrLblNama[i].setFont(new Font("Trebuchet MS", 0, 60));
                arrLblNama[i].setVisible(false);
                pnlBg.add(arrLblNama[i], 0);

                arrLblBagian[i] = new JLabel("");
                arrLblBagian[i].setBounds(1200, 270 + (i * margin), 496, 71);
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
                while (itr.hasNext()) {
                    Row row = itr.next();
                    if (rowNo == 1) {
                        Iterator<Cell> cellIterator = row.cellIterator();
                        if (cellIterator.hasNext()) {
                            XSSFCell cell = (XSSFCell) cellIterator.next();
                            judul = cell.getStringCellValue();
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
                                peserta = true;
                            } else if (cell.getCellTypeEnum() == CellType.FORMULA) {
                                if (cell.getCachedFormulaResultTypeEnum() == CellType.NUMERIC) {
                                    kloter = (int) cell.getNumericCellValue();
                                    peserta = true;
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
                btnMulaiUndian.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
        undian();
    }//GEN-LAST:event_formMouseClicked

    private void LblResetMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LblResetMouseClicked
        // TODO add your handling code here:
        if (LblReset.getText().equals("RESET")) {
            lblPilihFileExcel.setVisible(true);
            btnBrowse.setVisible(true);
            btnMulaiUndian.setVisible(false);
            lblBg.setVisible(false);
            Lbl_judul.setVisible(false);
            lblSelamat.setVisible(false);
            LblReset.setVisible(false);
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
        } else if (LblReset.getText().equals("ULANG")) {
            for (int i = 0; i < arrLblAnimasi.length; i++) {
                arrLblAnimasi[i].setVisible(false);
            }
            arrLblNo[(kloter - 1) % 10].setForeground(Color.BLACK);
            arrLblNama[(kloter - 1) % 10].setForeground(Color.BLACK);
            arrLblBagian[(kloter - 1) % 10].setForeground(Color.BLACK);
            kloter--;
            LblReset.setVisible(false);
            undian();
        }
    }//GEN-LAST:event_LblResetMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LblReset;
    private javax.swing.JLabel Lbl_judul;
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnMulaiUndian;
    private javax.swing.JFileChooser fcExcel;
    private javax.swing.JLabel lblPilihFileExcel;
    private javax.swing.JLabel lblSelamat;
    private javax.swing.JPanel pnlBg;
    // End of variables declaration//GEN-END:variables
}
