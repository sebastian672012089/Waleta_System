package waleta_system.BahanBaku;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import waleta_system.Class.ColumnsAutoSizer;
import waleta_system.Class.DataBahanBaku;
import waleta_system.Class.ExportToExcel;
import waleta_system.Class.StockBahanBaku;
import waleta_system.Class.Utility;
import waleta_system.Interface.InterfacePanel;
import waleta_system.MainForm;

public class JPanel_DataBahanBaku extends javax.swing.JPanel implements InterfacePanel {

    String sql = null;
    ResultSet rs;
    Date today = new Date();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    DefaultTableCellRenderer TableAlignment = new DefaultTableCellRenderer();
    DecimalFormat decimalFormat = new DecimalFormat();
    PreparedStatement pst;
    long min_sisa = 0;
    long val1BRSBRW1KPG = 0;
    long val1BRSBRW1GR = 0;
    long val1BRSBRW2KPG = 0;
    long val1BRSBRW2GR = 0;
    long val1BRSBRW3KPG = 0;
    long val1BRSBRW3GR = 0;
    long val1BRSBR_KPG = 0;
    long val1BRSBR_GR = 0;
    long val1BRW1KPG = 0;
    long val1BRW1GR = 0;
    long val1BRW2KPG = 0;
    long val1BRW2GR = 0;
    long val1BRW3KPG = 0;
    long val1BRW3GR = 0;
    long val1BR_KPG = 0;
    long val1BR_GR = 0;
    long val1BSW1KPG = 0;
    long val1BSW1GR = 0;
    long val1BSW2KPG = 0;
    long val1BSW2GR = 0;
    long val1BSW3KPG = 0;
    long val1BSW3GR = 0;
    long val1BS_KPG = 0;
    long val1BS_GR = 0;
    long val1BBW1KPG = 0;
    long val1BBW1GR = 0;
    long val1BBW2KPG = 0;
    long val1BBW2GR = 0;
    long val1BBW3KPG = 0;
    long val1BBW3GR = 0;
    long val1BB_KPG = 0;
    long val1BB_GR = 0;
    long val1BB2W1KPG = 0;
    long val1BB2W1GR = 0;
    long val1BB2W2KPG = 0;
    long val1BB2W2GR = 0;
    long val1BB2W3KPG = 0;
    long val1BB2W3GR = 0;
    long val1BB2_KPG = 0;
    long val1BB2_GR = 0;
    long val1_W1KPG = 0;
    long val1_W1GR = 0;
    long val1_W2KPG = 0;
    long val1_W2GR = 0;
    long val1_W3KPG = 0;
    long val1_W3GR = 0;
    long val1__KPG = 0;
    long val1__GR = 0;
    long val2BRSBRW1KPG = 0;
    long val2BRSBRW1GR = 0;
    long val2BRSBRW2KPG = 0;
    long val2BRSBRW2GR = 0;
    long val2BRSBRW3KPG = 0;
    long val2BRSBRW3GR = 0;
    long val2BRSBR_KPG = 0;
    long val2BRSBR_GR = 0;
    long val2BRW1KPG = 0;
    long val2BRW1GR = 0;
    long val2BRW2KPG = 0;
    long val2BRW2GR = 0;
    long val2BRW3KPG = 0;
    long val2BRW3GR = 0;
    long val2BR_KPG = 0;
    long val2BR_GR = 0;
    long val2BSW1KPG = 0;
    long val2BSW1GR = 0;
    long val2BSW2KPG = 0;
    long val2BSW2GR = 0;
    long val2BSW3KPG = 0;
    long val2BSW3GR = 0;
    long val2BS_KPG = 0;
    long val2BS_GR = 0;
    long val2BBW1KPG = 0;
    long val2BBW1GR = 0;
    long val2BBW2KPG = 0;
    long val2BBW2GR = 0;
    long val2BBW3KPG = 0;
    long val2BBW3GR = 0;
    long val2BB_KPG = 0;
    long val2BB_GR = 0;
    long val2BB2W1KPG = 0;
    long val2BB2W1GR = 0;
    long val2BB2W2KPG = 0;
    long val2BB2W2GR = 0;
    long val2BB2W3KPG = 0;
    long val2BB2W3GR = 0;
    long val2BB2_KPG = 0;
    long val2BB2_GR = 0;
    long val2_W1KPG = 0;
    long val2_W1GR = 0;
    long val2_W2KPG = 0;
    long val2_W2GR = 0;
    long val2_W3KPG = 0;
    long val2_W3GR = 0;
    long val2__KPG = 0;
    long val2__GR = 0;
    long val3BRSBRW1KPG = 0;
    long val3BRSBRW1GR = 0;
    long val3BRSBRW2KPG = 0;
    long val3BRSBRW2GR = 0;
    long val3BRSBRW3KPG = 0;
    long val3BRSBRW3GR = 0;
    long val3BRSBR_KPG = 0;
    long val3BRSBR_GR = 0;
    long val3BRW1KPG = 0;
    long val3BRW1GR = 0;
    long val3BRW2KPG = 0;
    long val3BRW2GR = 0;
    long val3BRW3KPG = 0;
    long val3BRW3GR = 0;
    long val3BR_KPG = 0;
    long val3BR_GR = 0;
    long val3BSW1KPG = 0;
    long val3BSW1GR = 0;
    long val3BSW2KPG = 0;
    long val3BSW2GR = 0;
    long val3BSW3KPG = 0;
    long val3BSW3GR = 0;
    long val3BS_KPG = 0;
    long val3BS_GR = 0;
    long val3BBW1KPG = 0;
    long val3BBW1GR = 0;
    long val3BBW2KPG = 0;
    long val3BBW2GR = 0;
    long val3BBW3KPG = 0;
    long val3BBW3GR = 0;
    long val3BB_KPG = 0;
    long val3BB_GR = 0;
    long val3BB2W1KPG = 0;
    long val3BB2W1GR = 0;
    long val3BB2W2KPG = 0;
    long val3BB2W2GR = 0;
    long val3BB2W3KPG = 0;
    long val3BB2W3GR = 0;
    long val3BB2_KPG = 0;
    long val3BB2_GR = 0;
    long val3_W1KPG = 0;
    long val3_W1GR = 0;
    long val3_W2KPG = 0;
    long val3_W2GR = 0;
    long val3_W3KPG = 0;
    long val3_W3GR = 0;
    long val3__KPG = 0;
    long val3__GR = 0;
    long valMkLainBRSBRW1KPG = 0;
    long valMkLainBRSBRW1GR = 0;
    long valMkLainBRSBRW2KPG = 0;
    long valMkLainBRSBRW2GR = 0;
    long valMkLainBRSBRW3KPG = 0;
    long valMkLainBRSBRW3GR = 0;
    long valMkLainBRSBR_KPG = 0;
    long valMkLainBRSBR_GR = 0;
    long valMkLainBRW1KPG = 0;
    long valMkLainBRW1GR = 0;
    long valMkLainBRW2KPG = 0;
    long valMkLainBRW2GR = 0;
    long valMkLainBRW3KPG = 0;
    long valMkLainBRW3GR = 0;
    long valMkLainBR_KPG = 0;
    long valMkLainBR_GR = 0;
    long valMkLainBSW1KPG = 0;
    long valMkLainBSW1GR = 0;
    long valMkLainBSW2KPG = 0;
    long valMkLainBSW2GR = 0;
    long valMkLainBSW3KPG = 0;
    long valMkLainBSW3GR = 0;
    long valMkLainBS_KPG = 0;
    long valMkLainBS_GR = 0;
    long valMkLainBBW1KPG = 0;
    long valMkLainBBW1GR = 0;
    long valMkLainBBW2KPG = 0;
    long valMkLainBBW2GR = 0;
    long valMkLainBBW3KPG = 0;
    long valMkLainBBW3GR = 0;
    long valMkLainBB_KPG = 0;
    long valMkLainBB_GR = 0;
    long valMkLainBB2W1KPG = 0;
    long valMkLainBB2W1GR = 0;
    long valMkLainBB2W2KPG = 0;
    long valMkLainBB2W2GR = 0;
    long valMkLainBB2W3KPG = 0;
    long valMkLainBB2W3GR = 0;
    long valMkLainBB2_KPG = 0;
    long valMkLainBB2_GR = 0;
    long valMkLain_W1KPG = 0;
    long valMkLain_W1GR = 0;
    long valMkLain_W2KPG = 0;
    long valMkLain_W2GR = 0;
    long valMkLain_W3KPG = 0;
    long valMkLain_W3GR = 0;
    long valMkLain__KPG = 0;
    long valMkLain__GR = 0;
    long valSgtgBRSBRW1KPG = 0;
    long valSgtgBRSBRW1GR = 0;
    long valSgtgBRSBRW2KPG = 0;
    long valSgtgBRSBRW2GR = 0;
    long valSgtgBRSBRW3KPG = 0;
    long valSgtgBRSBRW3GR = 0;
    long valSgtgBRSBR_KPG = 0;
    long valSgtgBRSBR_GR = 0;
    long valSgtgBRW1KPG = 0;
    long valSgtgBRW1GR = 0;
    long valSgtgBRW2KPG = 0;
    long valSgtgBRW2GR = 0;
    long valSgtgBRW3KPG = 0;
    long valSgtgBRW3GR = 0;
    long valSgtgBR_KPG = 0;
    long valSgtgBR_GR = 0;
    long valSgtgBSW1KPG = 0;
    long valSgtgBSW1GR = 0;
    long valSgtgBSW2KPG = 0;
    long valSgtgBSW2GR = 0;
    long valSgtgBSW3KPG = 0;
    long valSgtgBSW3GR = 0;
    long valSgtgBS_KPG = 0;
    long valSgtgBS_GR = 0;
    long valSgtgBBW1KPG = 0;
    long valSgtgBBW1GR = 0;
    long valSgtgBBW2KPG = 0;
    long valSgtgBBW2GR = 0;
    long valSgtgBBW3KPG = 0;
    long valSgtgBBW3GR = 0;
    long valSgtgBB_KPG = 0;
    long valSgtgBB_GR = 0;
    long valSgtgBB2W1KPG = 0;
    long valSgtgBB2W1GR = 0;
    long valSgtgBB2W2KPG = 0;
    long valSgtgBB2W2GR = 0;
    long valSgtgBB2W3KPG = 0;
    long valSgtgBB2W3GR = 0;
    long valSgtgBB2_KPG = 0;
    long valSgtgBB2_GR = 0;
    long valSgtg_W1KPG = 0;
    long valSgtg_W1GR = 0;
    long valSgtg_W2KPG = 0;
    long valSgtg_W2GR = 0;
    long valSgtg_W3KPG = 0;
    long valSgtg_W3GR = 0;
    long valSgtg__KPG = 0;
    long valSgtg__GR = 0;
    long valOvalBRSBRW1KPG = 0;
    long valOvalBRSBRW1GR = 0;
    long valOvalBRSBRW2KPG = 0;
    long valOvalBRSBRW2GR = 0;
    long valOvalBRSBRW3KPG = 0;
    long valOvalBRSBRW3GR = 0;
    long valOvalBRSBR_KPG = 0;
    long valOvalBRSBR_GR = 0;
    long valOvalBRW1KPG = 0;
    long valOvalBRW1GR = 0;
    long valOvalBRW2KPG = 0;
    long valOvalBRW2GR = 0;
    long valOvalBRW3KPG = 0;
    long valOvalBRW3GR = 0;
    long valOvalBR_KPG = 0;
    long valOvalBR_GR = 0;
    long valOvalBSW1KPG = 0;
    long valOvalBSW1GR = 0;
    long valOvalBSW2KPG = 0;
    long valOvalBSW2GR = 0;
    long valOvalBSW3KPG = 0;
    long valOvalBSW3GR = 0;
    long valOvalBS_KPG = 0;
    long valOvalBS_GR = 0;
    long valOvalBBW1KPG = 0;
    long valOvalBBW1GR = 0;
    long valOvalBBW2KPG = 0;
    long valOvalBBW2GR = 0;
    long valOvalBBW3KPG = 0;
    long valOvalBBW3GR = 0;
    long valOvalBB_KPG = 0;
    long valOvalBB_GR = 0;
    long valOvalBB2W1KPG = 0;
    long valOvalBB2W1GR = 0;
    long valOvalBB2W2KPG = 0;
    long valOvalBB2W2GR = 0;
    long valOvalBB2W3KPG = 0;
    long valOvalBB2W3GR = 0;
    long valOvalBB2_KPG = 0;
    long valOvalBB2_GR = 0;
    long valOval_W1KPG = 0;
    long valOval_W1GR = 0;
    long valOval_W2KPG = 0;
    long valOval_W2GR = 0;
    long valOval_W3KPG = 0;
    long valOval_W3GR = 0;
    long valOval__KPG = 0;
    long valOval__GR = 0;
    long valPchLbgBRSBRW1KPG = 0;
    long valPchLbgBRSBRW1GR = 0;
    long valPchLbgBRSBRW2KPG = 0;
    long valPchLbgBRSBRW2GR = 0;
    long valPchLbgBRSBRW3KPG = 0;
    long valPchLbgBRSBRW3GR = 0;
    long valPchLbgBRSBR_KPG = 0;
    long valPchLbgBRSBR_GR = 0;
    long valPchLbgBRW1KPG = 0;
    long valPchLbgBRW1GR = 0;
    long valPchLbgBRW2KPG = 0;
    long valPchLbgBRW2GR = 0;
    long valPchLbgBRW3KPG = 0;
    long valPchLbgBRW3GR = 0;
    long valPchLbgBR_KPG = 0;
    long valPchLbgBR_GR = 0;
    long valPchLbgBSW1KPG = 0;
    long valPchLbgBSW1GR = 0;
    long valPchLbgBSW2KPG = 0;
    long valPchLbgBSW2GR = 0;
    long valPchLbgBSW3KPG = 0;
    long valPchLbgBSW3GR = 0;
    long valPchLbgBS_KPG = 0;
    long valPchLbgBS_GR = 0;
    long valPchLbgBBW1KPG = 0;
    long valPchLbgBBW1GR = 0;
    long valPchLbgBBW2KPG = 0;
    long valPchLbgBBW2GR = 0;
    long valPchLbgBBW3KPG = 0;
    long valPchLbgBBW3GR = 0;
    long valPchLbgBB_KPG = 0;
    long valPchLbgBB_GR = 0;
    long valPchLbgBB2W1KPG = 0;
    long valPchLbgBB2W1GR = 0;
    long valPchLbgBB2W2KPG = 0;
    long valPchLbgBB2W2GR = 0;
    long valPchLbgBB2W3KPG = 0;
    long valPchLbgBB2W3GR = 0;
    long valPchLbgBB2_KPG = 0;
    long valPchLbgBB2_GR = 0;
    long valPchLbg_W1KPG = 0;
    long valPchLbg_W1GR = 0;
    long valPchLbg_W2KPG = 0;
    long valPchLbg_W2GR = 0;
    long valPchLbg_W3KPG = 0;
    long valPchLbg_W3GR = 0;
    long valPchLbg__KPG = 0;
    long valPchLbg__GR = 0;
    long valTotalW1KPG = 0;
    long valTotalW1GR = 0;
    long valTotalW2KPG = 0;
    long valTotalW2GR = 0;
    long valTotalW3KPG = 0;
    long valTotalW3GR = 0;
    long valTotal_KPG = 0;
    long valTotal_GR = 0;
    long valTotalTotalKPG = 0;
    long valTotalTotalGR = 0;
    long valTotal1BRSBRKPG = 0;
    long valTotal1BRSBRGR = 0;
    long valTotal1BRKPG = 0;
    long valTotal1BRGR = 0;
    long valTotal1BSKPG = 0;
    long valTotal1BSGR = 0;
    long valTotal1BBKPG = 0;
    long valTotal1BBGR = 0;
    long valTotal1BB2KPG = 0;
    long valTotal1BB2GR = 0;
    long valTotal1_KPG = 0;
    long valTotal1_GR = 0;
    long valTotal2BRSBRKPG = 0;
    long valTotal2BRSBRGR = 0;
    long valTotal2BRKPG = 0;
    long valTotal2BRGR = 0;
    long valTotal2BSKPG = 0;
    long valTotal2BSGR = 0;
    long valTotal2BBKPG = 0;
    long valTotal2BBGR = 0;
    long valTotal2BB2KPG = 0;
    long valTotal2BB2GR = 0;
    long valTotal2_KPG = 0;
    long valTotal2_GR = 0;
    long valTotal3BRSBRKPG = 0;
    long valTotal3BRSBRGR = 0;
    long valTotal3BRKPG = 0;
    long valTotal3BRGR = 0;
    long valTotal3BSKPG = 0;
    long valTotal3BSGR = 0;
    long valTotal3BBKPG = 0;
    long valTotal3BBGR = 0;
    long valTotal3BB2KPG = 0;
    long valTotal3BB2GR = 0;
    long valTotal3_KPG = 0;
    long valTotal3_GR = 0;
    long valTotalMkLainBRSBRKPG = 0;
    long valTotalMkLainBRSBRGR = 0;
    long valTotalMkLainBRKPG = 0;
    long valTotalMkLainBRGR = 0;
    long valTotalMkLainBSKPG = 0;
    long valTotalMkLainBSGR = 0;
    long valTotalMkLainBBKPG = 0;
    long valTotalMkLainBBGR = 0;
    long valTotalMkLainBB2KPG = 0;
    long valTotalMkLainBB2GR = 0;
    long valTotalMkLain_KPG = 0;
    long valTotalMkLain_GR = 0;
    long valTotalSgtgBRSBRKPG = 0;
    long valTotalSgtgBRSBRGR = 0;
    long valTotalSgtgBRKPG = 0;
    long valTotalSgtgBRGR = 0;
    long valTotalSgtgBSKPG = 0;
    long valTotalSgtgBSGR = 0;
    long valTotalSgtgBBKPG = 0;
    long valTotalSgtgBBGR = 0;
    long valTotalSgtgBB2KPG = 0;
    long valTotalSgtgBB2GR = 0;
    long valTotalSgtg_KPG = 0;
    long valTotalSgtg_GR = 0;
    long valTotalOvalBRSBRKPG = 0;
    long valTotalOvalBRSBRGR = 0;
    long valTotalOvalBRKPG = 0;
    long valTotalOvalBRGR = 0;
    long valTotalOvalBSKPG = 0;
    long valTotalOvalBSGR = 0;
    long valTotalOvalBBKPG = 0;
    long valTotalOvalBBGR = 0;
    long valTotalOvalBB2KPG = 0;
    long valTotalOvalBB2GR = 0;
    long valTotalOval_KPG = 0;
    long valTotalOval_GR = 0;
    long valTotalPchLbgBRSBRKPG = 0;
    long valTotalPchLbgBRSBRGR = 0;
    long valTotalPchLbgBRKPG = 0;
    long valTotalPchLbgBRGR = 0;
    long valTotalPchLbgBSKPG = 0;
    long valTotalPchLbgBSGR = 0;
    long valTotalPchLbgBBKPG = 0;
    long valTotalPchLbgBBGR = 0;
    long valTotalPchLbgBB2KPG = 0;
    long valTotalPchLbgBB2GR = 0;
    long valTotalPchLbg_KPG = 0;
    long valTotalPchLbg_GR = 0;
    long val_W1KPG = 0;
    long val_W1GR = 0;
    long val_W2KPG = 0;
    long val_W2GR = 0;
    long val_W3KPG = 0;
    long val_W3GR = 0;
    long val__KPG = 0;
    long val__GR = 0;
    long val_TotalKPG = 0;
    long val_TotalGR = 0;

    public JPanel_DataBahanBaku() {
        initComponents();
    }

    @Override
    public void init() {
        refreshTable_BahanBaku();
        refreshTable_Stock_grade();
        button_search_stock.setIcon(Utility.ResizeImageIcon(new javax.swing.ImageIcon(getClass().getResource("/waleta_system/Images/search_icon.png")), button_search_stock.getWidth(), button_search_stock.getHeight()));
        try {
            sql = "SELECT `kode_grade` FROM `tb_grade_bahan_baku` ORDER BY `kode_grade`";
            rs = Utility.db.getStatement().executeQuery(sql);
            ComboBox_Search_grade.removeAllItems();
            ComboBox_Search_grade.addItem("All");
            while (rs.next()) {
                ComboBox_Search_grade.addItem(rs.getString("kode_grade"));
            }

            sql = "SELECT DISTINCT(`jenis_bulu`) AS 'jenis_bulu' FROM `tb_grade_bahan_baku` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            ComboBox_FilterBulu.removeAllItems();
            ComboBox_FilterBulu.addItem("All");
            while (rs.next()) {
                ComboBox_FilterBulu.addItem(rs.getString("jenis_bulu"));
            }
            sql = "SELECT DISTINCT(`jenis_warna`) AS 'jenis_warna' FROM `tb_grade_bahan_baku` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            ComboBox_FilterWarna.removeAllItems();
            ComboBox_FilterWarna.addItem("All");
            while (rs.next()) {
                ComboBox_FilterWarna.addItem(rs.getString("jenis_warna"));
            }
            sql = "SELECT DISTINCT(`kategori_proses`) AS 'kategori_proses' FROM `tb_grade_bahan_baku` WHERE 1";
            rs = Utility.db.getStatement().executeQuery(sql);
            ComboBox_FilterKategori.removeAllItems();
            ComboBox_FilterKategori.addItem("All");
            while (rs.next()) {
                ComboBox_FilterKategori.addItem(rs.getString("kategori_proses"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e);
        }
        AutoCompleteDecorator.decorate(ComboBox_Search_grade);
        table_stok_per_kartu.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_stok_per_kartu.getSelectedRow() != -1) {
                    Table_stock_bahan_baku.clearSelection();
                    int i = table_stok_per_kartu.getSelectedRow();
                    label_no_kartu.setText(table_stok_per_kartu.getValueAt(i, 0).toString());
                    refreshTable_LP();
                    refreshTable_Keluar();
                    refreshTable_DataCMP();
                    refreshTable_Stock_grade();
                }
            }
        });
        Table_stock_bahan_baku.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && Table_stock_bahan_baku.getSelectedRow() != -1) {
                    table_stok_per_kartu.clearSelection();
                    refreshTable_LP();
                    refreshTable_Keluar();
                    refreshTable_DataCMP();
                }
            }
        });

        table_stok_per_grade.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table_stok_per_grade.getSelectedRow() != -1) {
                    label_grade.setText(table_stok_per_grade.getValueAt(table_stok_per_grade.getSelectedRow(), 0).toString());
                    refresh_TabelKartuMasuk();
                }
            }
        });
    }

    public ArrayList<DataBahanBaku> BahanBakuList() {
        ArrayList<DataBahanBaku> BahanBakuList = new ArrayList<>();
        try {
            if (Date_FilterStok.getDate() != null) {
                sql = "SELECT `tb_bahan_baku_masuk`.`no_kartu_waleta`, `tb_supplier`.`nama_supplier`, `tb_rumah_burung`.`nama_rumah_burung`, `tgl_masuk`, `tgl_panen`, `tgl_grading`, `tgl_timbang`, `tb_lab_bahan_baku`.`nitrit_bm_w3`, `kadar_air_bahan_baku`, `berat_awal`, `berat_real`, `keping_real`, SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_grading_bahan_baku`.`total_berat`) AS 'total_harga', `last_stok` \n"
                        + "FROM `tb_bahan_baku_masuk` \n"
                        + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi`=`tb_rumah_burung`.`no_registrasi`\n"
                        + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier`=`tb_supplier`.`kode_supplier` \n"
                        + "LEFT JOIN `tb_lab_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_lab_bahan_baku`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta`\n"
                        + "WHERE "
                        + "`tb_bahan_baku_masuk`.`no_kartu_waleta` LIKE '%" + txt_search_data_bahan_baku.getText() + "%' "
                        + "AND `tgl_masuk`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'\n"
                        + "GROUP BY(`tb_bahan_baku_masuk`.`no_kartu_waleta`)";
            } else {
                sql = "SELECT `tb_bahan_baku_masuk`.`no_kartu_waleta`, `tb_supplier`.`nama_supplier`, `tb_rumah_burung`.`nama_rumah_burung`, `tgl_masuk`, `tgl_panen`, `tgl_grading`, `tgl_timbang`, `tb_lab_bahan_baku`.`nitrit_bm_w3`, `kadar_air_bahan_baku`, `berat_awal`, `berat_real`, `keping_real`, SUM(`tb_grading_bahan_baku`.`harga_bahanbaku` * `tb_grading_bahan_baku`.`total_berat`) AS 'total_harga', `last_stok` \n"
                        + "FROM `tb_bahan_baku_masuk` \n"
                        + "LEFT JOIN `tb_rumah_burung` ON `tb_bahan_baku_masuk`.`no_registrasi`=`tb_rumah_burung`.`no_registrasi`\n"
                        + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier`=`tb_supplier`.`kode_supplier` \n"
                        + "LEFT JOIN `tb_lab_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_lab_bahan_baku`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_bahan_baku_masuk`.`no_kartu_waleta` = `tb_grading_bahan_baku`.`no_kartu_waleta`\n"
                        + "WHERE "
                        + "`tb_bahan_baku_masuk`.`no_kartu_waleta` LIKE '%" + txt_search_data_bahan_baku.getText() + "%'\n"
                        + "GROUP BY(`tb_bahan_baku_masuk`.`no_kartu_waleta`)";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            DataBahanBaku BahanBaku;
            while (rs.next()) {
                BahanBaku = new DataBahanBaku(rs.getString("no_kartu_waleta"),
                        rs.getString("nama_supplier"),
                        rs.getString("nama_rumah_burung"),
                        rs.getDate("tgl_masuk"),
                        rs.getDate("tgl_panen"),
                        rs.getDate("tgl_grading"),
                        rs.getDate("tgl_timbang"),
                        rs.getInt("nitrit_bm_w3"),
                        rs.getFloat("kadar_air_bahan_baku"),
                        rs.getInt("berat_awal"),
                        rs.getInt("berat_real"),
                        rs.getInt("keping_real"),
                        0,
                        rs.getInt("last_stok")
                );
                BahanBakuList.add(BahanBaku);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BahanBakuList;
    }

    public void refreshTable_BahanBaku() {
        try {
            String sql_lp = null, sql_jual = null, sql_cmp = null;
            int berat_lp = 0, keping_lp = 0, berat_keluar = 0, keping_keluar = 0, berat_cmp = 0, keping_cmp = 0;
            int total_stok_kpg = 0, total_stok_gram = 0;
            ResultSet rs_lp, rs_keluar, rs_cmp;
            ArrayList<DataBahanBaku> list = BahanBakuList();
            DefaultTableModel model = (DefaultTableModel) table_stok_per_kartu.getModel();
            model.setRowCount(0);
            Object[] row = new Object[20];
            for (int i = 0; i < list.size(); i++) {
                int stok_kpg = 0, stok_gram = 0;
//                if (list.get(i).getLast_stok() > 0) {
                    if (Date_FilterStok.getDate() != null) {
                        sql_lp = "SELECT SUM(`tb_laporan_produksi`.`berat_basah`) AS 'berat_lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping_lp' "
                                + "FROM `tb_laporan_produksi` "
                                + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' "
                                + "AND `tanggal_lp`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";

                        sql_jual = "SELECT SUM(`tb_bahan_baku_keluar`.`total_berat_keluar`) AS 'berat_keluar', SUM(`tb_bahan_baku_keluar`.`total_keping_keluar`) AS 'keping_keluar' "
                                + "FROM `tb_bahan_baku_keluar` "
                                + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`"
                                + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' "
                                + "AND `tgl_keluar`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";

                        sql_cmp = "SELECT SUM(`keping`) AS 'keping_cmp', SUM(`gram`) AS 'berat_cmp' \n"
                                + "FROM `tb_kartu_cmp_detail` "
                                + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                                + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                                + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' "
                                + "AND `tanggal`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                    } else {
                        sql_lp = "SELECT SUM(`tb_laporan_produksi`.`berat_basah`) AS 'berat_lp', SUM(`tb_laporan_produksi`.`jumlah_keping`) AS 'keping_lp' "
                                + "FROM `tb_laporan_produksi` "
                                + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "'";

                        sql_jual = "SELECT SUM(`tb_bahan_baku_keluar`.`total_berat_keluar`) AS 'berat_keluar', SUM(`tb_bahan_baku_keluar`.`total_keping_keluar`) AS 'keping_keluar' "
                                + "FROM `tb_bahan_baku_keluar` "
                                + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "'";

                        sql_cmp = "SELECT SUM(`keping`) AS 'keping_cmp', SUM(`gram`) AS 'berat_cmp' "
                                + "FROM `tb_kartu_cmp_detail`\n"
                                + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                                + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "'";
                    }

                    rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                    if (rs_lp.next()) {
                        berat_lp = rs_lp.getInt("berat_lp");
                        keping_lp = rs_lp.getInt("keping_lp");
                    } else {
                        berat_lp = 0;
                        keping_lp = 0;
                    }

                    rs_keluar = Utility.db.getStatement().executeQuery(sql_jual);
                    if (rs_keluar.next()) {
                        berat_keluar = rs_keluar.getInt("berat_keluar");
                        keping_keluar = rs_keluar.getInt("keping_keluar");
                    } else {
                        berat_keluar = 0;
                        keping_keluar = 0;
                    }

                    rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                    if (rs_cmp.next()) {
                        berat_cmp = rs_cmp.getInt("berat_cmp");
                        keping_cmp = rs_cmp.getInt("keping_cmp");
                    } else {
                        berat_cmp = 0;
                        keping_cmp = 0;
                    }
                    stok_kpg = list.get(i).getKeping_real() - (keping_lp + keping_keluar + keping_cmp);
                    stok_gram = list.get(i).getBerat_real() - (berat_lp + berat_keluar + berat_cmp);
//                }

                row[0] = list.get(i).getNo_kartu_waleta();
                row[1] = list.get(i).getNama_supplier();
                row[2] = list.get(i).getNama_rumah_burung();
                row[3] = list.get(i).getTgl_masuk();
                row[4] = list.get(i).getTgl_panen();
                row[5] = list.get(i).getTgl_grading();
                row[6] = list.get(i).getTgl_timbang();
                row[7] = list.get(i).getNitrit_bahan_mentah();
                row[8] = list.get(i).getKadar_air_bahan_baku();
                row[9] = list.get(i).getBerat_asal();
                row[10] = list.get(i).getKeping_real();
                row[11] = list.get(i).getBerat_real();
                row[12] = stok_kpg;
                row[13] = stok_gram;
                row[14] = list.get(i).getLast_stok();
                model.addRow(row);
                total_stok_kpg = total_stok_kpg + stok_kpg;
                total_stok_gram = total_stok_gram + stok_gram;
            }
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_per_kartu);
            label_total_stok_kpg.setText(decimalFormat.format(total_stok_kpg));
            label_total_stok_gram.setText(decimalFormat.format(total_stok_gram));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<StockBahanBaku> StockList() {
        ArrayList<StockBahanBaku> StockList = new ArrayList<>();
        try {
            String no_kartu;
            int i = table_stok_per_kartu.getSelectedRow();
            if (i == -1) {
                no_kartu = null;
            } else {
                no_kartu = table_stok_per_kartu.getValueAt(i, 0).toString();
            }
            String kode = ComboBox_Search_grade.getSelectedItem().toString();
            if (ComboBox_Search_grade.getSelectedItem().equals("All")) {
                kode = "";
            }
            if (CheckBox_show_All_Cards.isSelected()) {
                if (Date_FilterStok.getDate() != null) {
                    sql = "SELECT `tb_grading_bahan_baku`.`no_grading`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `tb_grading_bahan_baku`.`jumlah_keping`, `total_berat` \n"
                            + "FROM `tb_grading_bahan_baku` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                            + "WHERE `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + kode + "%' AND `tb_bahan_baku_masuk`.`tgl_masuk`<='" + dateFormat.format(Date_FilterStok.getDate()) + "' \n"
                            + "GROUP BY `no_grading`";
                } else {
                    sql = "SELECT `tb_grading_bahan_baku`.`no_grading`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `tb_grading_bahan_baku`.`jumlah_keping`, `total_berat` \n"
                            + "FROM `tb_grading_bahan_baku`\n"
                            + "WHERE `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + kode + "%'\n"
                            + "GROUP BY `no_grading`";
                }
            } else {
                sql = "SELECT `tb_grading_bahan_baku`.`no_grading`, `tb_grading_bahan_baku`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `tb_grading_bahan_baku`.`jumlah_keping`, `total_berat` \n"
                        + "FROM `tb_grading_bahan_baku`\n"
                        + "WHERE `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + kode + "%' AND `tb_grading_bahan_baku`.`no_kartu_waleta`='" + no_kartu + "'\n"
                        + "GROUP BY `no_grading`";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            StockBahanBaku Stock;
            while (rs.next()) {
                Stock = new StockBahanBaku(rs.getString("no_grading"), rs.getString("no_kartu_waleta"), null, rs.getString("kode_grade"), rs.getInt("jumlah_keping"), rs.getInt("total_berat"), 0);
                StockList.add(Stock);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return StockList;
    }

    public void refreshTable_Stock_grade() {
        ResultSet rs_jual, rs_lp, rs_cmp;
        String sql_lp = null, sql_jual = null, sql_cmp = null;
        int kpg_lp = 0, gram_lp = 0, kpg_jual = 0, gram_jual = 0, kpg_cmp = 0, gram_cmp = 0;
        int sisa_kpg = 0, sisa_gram = 0;
        int total_sisa_kpg = 0, total_sisa_gram = 0;
        ArrayList<StockBahanBaku> list = StockList();
        DefaultTableModel model = (DefaultTableModel) Table_stock_bahan_baku.getModel();
        model.setRowCount(0);
        Object[] row = new Object[6];
        for (int i = 0; i < list.size(); i++) {
            try {
                if (Date_FilterStok.getDate() != null) {
                    sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' FROM `tb_laporan_produksi` "
                            + "WHERE `kode_grade` = '" + list.get(i).getKode_grade() + "'  AND `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `tanggal_lp`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                    sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' FROM `tb_bahan_baku_keluar` "
                            + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`"
                            + "WHERE `kode_grade` = '" + list.get(i).getKode_grade() + "'  AND `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "' AND `tb_bahan_baku_keluar1`.`tgl_keluar`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                    sql_cmp = "SELECT SUM(`keping`) AS 'keping', SUM(`gram`) AS 'berat' FROM `tb_kartu_cmp_detail` LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                            + "WHERE `no_grading` = '" + list.get(i).getNo_grading() + "' AND `tb_kartu_cmp`.`tanggal`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
                } else {
                    sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' FROM `tb_laporan_produksi` "
                            + "WHERE `kode_grade` = '" + list.get(i).getKode_grade() + "'  AND `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "'";
                    sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' FROM `tb_bahan_baku_keluar` "
                            + "WHERE `kode_grade` = '" + list.get(i).getKode_grade() + "'  AND `no_kartu_waleta` = '" + list.get(i).getNo_kartu_waleta() + "'";
                    sql_cmp = "SELECT SUM(`keping`) AS 'keping', SUM(`gram`) AS 'berat' FROM `tb_kartu_cmp_detail` LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                            + "WHERE `no_grading` = '" + list.get(i).getNo_grading() + "'";
                }

                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    kpg_lp = rs_lp.getInt("keping");
                    gram_lp = rs_lp.getInt("berat");
                }

                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    kpg_jual = rs_jual.getInt("keping");
                    gram_jual = rs_jual.getInt("berat");
                }

                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    kpg_cmp = rs_cmp.getInt("keping");
                    gram_cmp = rs_cmp.getInt("berat");
                }
            } catch (SQLException e) {
                Logger.getLogger(JPanel_DataBahanBaku.class.getName()).log(Level.SEVERE, null, e);
            }
            row[0] = list.get(i).getNo_kartu_waleta();
            row[1] = list.get(i).getKode_grade();
            row[2] = list.get(i).getJumlah_keping();
            row[3] = list.get(i).getTotal_berat();
            row[4] = list.get(i).getJumlah_keping() - (kpg_lp + kpg_jual + kpg_cmp);
            row[5] = list.get(i).getTotal_berat() - (gram_lp + gram_jual + gram_cmp);
            sisa_gram = list.get(i).getTotal_berat() - (gram_lp + gram_jual + gram_cmp);

            if (CheckBox_show_dataMasihSisa.isSelected()) {
                if (sisa_gram > 0) {
                    model.addRow(row);
                }
            } else {
                model.addRow(row);
            }
        }
        ColumnsAutoSizer.sizeColumnsToFit(Table_stock_bahan_baku);

        for (int i = 0; i < Table_stock_bahan_baku.getRowCount(); i++) {
            total_sisa_kpg = total_sisa_kpg + (int) Table_stock_bahan_baku.getValueAt(i, 4);
            total_sisa_gram = total_sisa_gram + (int) Table_stock_bahan_baku.getValueAt(i, 5);
        }
        label_total_sisa_kpg.setText(Integer.toString(total_sisa_kpg));
        label_total_sisa_gram.setText(Integer.toString(total_sisa_gram));
    }

    public void refreshTable_LP() {
        try {
            DefaultTableModel model = (DefaultTableModel) tb_LP.getModel();
            model.setRowCount(0);
            int total_gram = 0, total_keping = 0;
            String no_kartu = "", grade = "";
            int i = table_stok_per_kartu.getSelectedRow();
            int j = Table_stock_bahan_baku.getSelectedRow();
            sql = "";
            if (i == -1) {
                no_kartu = Table_stock_bahan_baku.getValueAt(j, 0).toString();
                grade = Table_stock_bahan_baku.getValueAt(j, 1).toString();
            } else if (j == -1) {
                no_kartu = table_stok_per_kartu.getValueAt(i, 0).toString();
            }
            if (Date_LP1.getDate() != null && Date_LP2.getDate() != null && Date_FilterStok.getDate() == null) {
                sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `berat_basah`, `berat_kering`, `tb_laporan_produksi`.`jumlah_keping`, `jumlah_gumpil`, `hilang_kaki_lp`, `hilang_ujung_lp`, `jumlah_sobek`, `pecah_1_lp`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_laporan_produksi` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + no_kartu + "%' AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + grade + "%' AND (`tanggal_lp` BETWEEN '" + dateFormat.format(Date_LP1.getDate()) + "' AND '" + dateFormat.format(Date_LP2.getDate()) + "')";
            } else if ((Date_LP1.getDate() == null || Date_LP2.getDate() == null) && Date_FilterStok.getDate() != null) {
                sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `berat_basah`, `berat_kering`, `tb_laporan_produksi`.`jumlah_keping`, `jumlah_gumpil`, `hilang_kaki_lp`, `hilang_ujung_lp`, `jumlah_sobek`, `pecah_1_lp`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_laporan_produksi` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + no_kartu + "%' AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + grade + "%' AND `tanggal_lp`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
            } else if (Date_LP1.getDate() == null && Date_LP2.getDate() == null && Date_FilterStok.getDate() == null) {
                sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `berat_basah`, `berat_kering`, `tb_laporan_produksi`.`jumlah_keping`, `jumlah_gumpil`, `hilang_kaki_lp`, `hilang_ujung_lp`, `jumlah_sobek`, `pecah_1_lp`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_laporan_produksi` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + no_kartu + "%' AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + grade + "%'";
            } else if (Date_LP1.getDate() != null && Date_LP2.getDate() != null && Date_FilterStok.getDate() != null) {
                JOptionPane.showMessageDialog(this, "Maaf untuk filter tanggal harap di pilih salah satu");
                Date_LP1.setDate(null);
                Date_LP2.setDate(null);
                sql = "SELECT `no_laporan_produksi`, `tb_laporan_produksi`.`no_kartu_waleta`, `tanggal_lp`, `ruangan`, `tb_laporan_produksi`.`kode_grade`, `memo_lp`, `tb_bahan_baku_masuk`.`kadar_air_bahan_baku`, `berat_basah`, `berat_kering`, `tb_laporan_produksi`.`jumlah_keping`, `jumlah_gumpil`, `hilang_kaki_lp`, `hilang_ujung_lp`, `jumlah_sobek`, `pecah_1_lp`, `tb_grading_bahan_baku`.`harga_bahanbaku` \n"
                        + "FROM `tb_laporan_produksi` LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_laporan_produksi`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_laporan_produksi`.`kode_grade`)"
                        + "WHERE `tb_laporan_produksi`.`no_kartu_waleta` LIKE '%" + no_kartu + "%' AND `tb_laporan_produksi`.`kode_grade` LIKE '%" + grade + "%' AND `tanggal_lp`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[6];
            while (rs.next()) {
                row[0] = rs.getString("no_laporan_produksi");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getDate("tanggal_lp");
                row[3] = rs.getString("ruangan");
                row[4] = rs.getInt("jumlah_keping");
                total_keping = total_keping + rs.getInt("jumlah_keping");
                row[5] = rs.getInt("berat_basah");
                total_gram = total_gram + rs.getInt("berat_basah");
                model.addRow(row);
            }

            ColumnsAutoSizer.sizeColumnsToFit(tb_LP);
            int rowData = tb_LP.getRowCount();
            label_total_lp.setText(Integer.toString(rowData));
            label_keping_lp.setText(Integer.toString(total_keping));
            label_berat_lp.setText(Integer.toString(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_Keluar() {
        try {
            int total_gram = 0, total_keping = 0;
            DefaultTableModel model = (DefaultTableModel) tb_keluar.getModel();
            model.setRowCount(0);
            String no_kartu = "", grade = "";
            int i = table_stok_per_kartu.getSelectedRow();
            int j = Table_stock_bahan_baku.getSelectedRow();
            sql = "";
            if (i == -1) {
                no_kartu = Table_stock_bahan_baku.getValueAt(j, 0).toString();
                grade = Table_stock_bahan_baku.getValueAt(j, 1).toString();
            } else if (j == -1) {
                no_kartu = table_stok_per_kartu.getValueAt(i, 0).toString();
            }
            if (Date_FilterStok.getDate() != null) {
                sql = "SELECT `tb_bahan_baku_keluar`.`kode_pengeluaran`, `tgl_keluar`, `customer_baku`, `tb_bahan_baku_keluar`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `total_keping_keluar`, `total_berat_keluar`, `tb_grading_bahan_baku`.`harga_bahanbaku` FROM `tb_bahan_baku_keluar` "
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)"
                        + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta`='" + no_kartu + "' AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + grade + "%' AND `tgl_keluar`<='" + dateFormat.format(Date_FilterStok.getDate()) + "'";
            } else {
                sql = "SELECT `tb_bahan_baku_keluar`.`kode_pengeluaran`, `tgl_keluar`, `customer_baku`, `tb_bahan_baku_keluar`.`no_kartu_waleta`, `tb_grading_bahan_baku`.`kode_grade`, `total_keping_keluar`, `total_berat_keluar`, `tb_grading_bahan_baku`.`harga_bahanbaku` FROM `tb_bahan_baku_keluar` "
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON (`tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_keluar`.`no_kartu_waleta` AND `tb_grading_bahan_baku`.`kode_grade` = `tb_bahan_baku_keluar`.`kode_grade`)"
                        + "WHERE `tb_bahan_baku_keluar`.`no_kartu_waleta`='" + no_kartu + "' AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + grade + "%'";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[7];
            while (rs.next()) {
                row[0] = rs.getString("kode_pengeluaran");
                row[1] = rs.getString("no_kartu_waleta");
                row[2] = rs.getString("kode_grade");
                row[3] = rs.getDate("tgl_keluar");
                row[4] = rs.getString("customer_baku");
                row[5] = rs.getInt("total_keping_keluar");
                row[6] = rs.getInt("total_berat_keluar");
                total_keping = total_keping + rs.getInt("total_keping_keluar");
                total_gram = total_gram + rs.getInt("total_berat_keluar");
//                row[4] = rs.getInt("jumlah_keping") - rs.getInt("total_keping_keluar");
//                row[5] = rs.getInt("total_berat") - rs.getInt("total_berat_keluar");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tb_keluar);
            int rowData = tb_keluar.getRowCount();
            label_total_keluar.setText(Integer.toString(rowData));
            label_keping_keluar.setText(Integer.toString(total_keping));
            label_berat_keluar.setText(Integer.toString(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshTable_DataCMP() {
        try {
            int total_gram = 0, total_keping = 0;
            DefaultTableModel model = (DefaultTableModel) tb_cmp.getModel();
            model.setRowCount(0);
            String no_kartu = "", grade = "";
            int i = table_stok_per_kartu.getSelectedRow();
            int j = Table_stock_bahan_baku.getSelectedRow();
            sql = "";
            if (i == -1) {
                no_kartu = Table_stock_bahan_baku.getValueAt(j, 0).toString();
                grade = Table_stock_bahan_baku.getValueAt(j, 1).toString();
            } else if (j == -1) {
                no_kartu = table_stok_per_kartu.getValueAt(i, 0).toString();
            }
            if (Date_FilterStok.getDate() != null) {
                sql = "SELECT `tb_kartu_cmp_detail`.`kode_kartu_cmp`, `tb_grading_bahan_baku`.`kode_grade`, `tb_kartu_cmp`.`tanggal`, `keping`, `gram`, `harga_bahanbaku` \n"
                        + "FROM `tb_kartu_cmp_detail` \n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + no_kartu + "' AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + grade + "%' AND `tb_kartu_cmp`.`tanggal` <= '" + dateFormat.format(Date_FilterStok.getDate()) + "'";
            } else {
                sql = "SELECT `tb_kartu_cmp_detail`.`kode_kartu_cmp`, `tb_grading_bahan_baku`.`kode_grade`, `tb_kartu_cmp`.`tanggal`, `keping`, `gram`, `harga_bahanbaku` \n"
                        + "FROM `tb_kartu_cmp_detail` \n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`\n"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE `tb_grading_bahan_baku`.`no_kartu_waleta` = '" + no_kartu + "' AND `tb_grading_bahan_baku`.`kode_grade` LIKE '%" + grade + "%'";
            }
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[5];
            while (rs.next()) {
                row[0] = rs.getString("kode_kartu_cmp");
                row[1] = rs.getString("kode_grade");
                row[2] = rs.getDate("tanggal");
                row[3] = rs.getInt("keping");
                row[4] = rs.getInt("gram");
                total_keping = total_keping + rs.getInt("keping");
                total_gram = total_gram + rs.getInt("gram");
                model.addRow(row);
            }
            ColumnsAutoSizer.sizeColumnsToFit(tb_cmp);

            int rowData = tb_cmp.getRowCount();
            label_total_cmp.setText(Integer.toString(rowData));
            label_keping_cmp.setText(Integer.toString(total_keping));
            label_berat_cmp.setText(Integer.toString(total_gram));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //BAGIAN TAB 2
    public void refresh_STOK_PER_GRADE() {
        try {
            val1BRSBRW1KPG = 0;
            val1BRSBRW1GR = 0;
            val1BRSBRW2KPG = 0;
            val1BRSBRW2GR = 0;
            val1BRSBRW3KPG = 0;
            val1BRSBRW3GR = 0;
            val1BRSBR_KPG = 0;
            val1BRSBR_GR = 0;
            val1BRW1KPG = 0;
            val1BRW1GR = 0;
            val1BRW2KPG = 0;
            val1BRW2GR = 0;
            val1BRW3KPG = 0;
            val1BRW3GR = 0;
            val1BR_KPG = 0;
            val1BR_GR = 0;
            val1BSW1KPG = 0;
            val1BSW1GR = 0;
            val1BSW2KPG = 0;
            val1BSW2GR = 0;
            val1BSW3KPG = 0;
            val1BSW3GR = 0;
            val1BS_KPG = 0;
            val1BS_GR = 0;
            val1BBW1KPG = 0;
            val1BBW1GR = 0;
            val1BBW2KPG = 0;
            val1BBW2GR = 0;
            val1BBW3KPG = 0;
            val1BBW3GR = 0;
            val1BB_KPG = 0;
            val1BB_GR = 0;
            val1BB2W1KPG = 0;
            val1BB2W1GR = 0;
            val1BB2W2KPG = 0;
            val1BB2W2GR = 0;
            val1BB2W3KPG = 0;
            val1BB2W3GR = 0;
            val1BB2_KPG = 0;
            val1BB2_GR = 0;
            val1_W1KPG = 0;
            val1_W1GR = 0;
            val1_W2KPG = 0;
            val1_W2GR = 0;
            val1_W3KPG = 0;
            val1_W3GR = 0;
            val1__KPG = 0;
            val1__GR = 0;
            val2BRSBRW1KPG = 0;
            val2BRSBRW1GR = 0;
            val2BRSBRW2KPG = 0;
            val2BRSBRW2GR = 0;
            val2BRSBRW3KPG = 0;
            val2BRSBRW3GR = 0;
            val2BRSBR_KPG = 0;
            val2BRSBR_GR = 0;
            val2BRW1KPG = 0;
            val2BRW1GR = 0;
            val2BRW2KPG = 0;
            val2BRW2GR = 0;
            val2BRW3KPG = 0;
            val2BRW3GR = 0;
            val2BR_KPG = 0;
            val2BR_GR = 0;
            val2BSW1KPG = 0;
            val2BSW1GR = 0;
            val2BSW2KPG = 0;
            val2BSW2GR = 0;
            val2BSW3KPG = 0;
            val2BSW3GR = 0;
            val2BS_KPG = 0;
            val2BS_GR = 0;
            val2BBW1KPG = 0;
            val2BBW1GR = 0;
            val2BBW2KPG = 0;
            val2BBW2GR = 0;
            val2BBW3KPG = 0;
            val2BBW3GR = 0;
            val2BB_KPG = 0;
            val2BB_GR = 0;
            val2BB2W1KPG = 0;
            val2BB2W1GR = 0;
            val2BB2W2KPG = 0;
            val2BB2W2GR = 0;
            val2BB2W3KPG = 0;
            val2BB2W3GR = 0;
            val2BB2_KPG = 0;
            val2BB2_GR = 0;
            val2_W1KPG = 0;
            val2_W1GR = 0;
            val2_W2KPG = 0;
            val2_W2GR = 0;
            val2_W3KPG = 0;
            val2_W3GR = 0;
            val2__KPG = 0;
            val2__GR = 0;
            val3BRSBRW1KPG = 0;
            val3BRSBRW1GR = 0;
            val3BRSBRW2KPG = 0;
            val3BRSBRW2GR = 0;
            val3BRSBRW3KPG = 0;
            val3BRSBRW3GR = 0;
            val3BRSBR_KPG = 0;
            val3BRSBR_GR = 0;
            val3BRW1KPG = 0;
            val3BRW1GR = 0;
            val3BRW2KPG = 0;
            val3BRW2GR = 0;
            val3BRW3KPG = 0;
            val3BRW3GR = 0;
            val3BR_KPG = 0;
            val3BR_GR = 0;
            val3BSW1KPG = 0;
            val3BSW1GR = 0;
            val3BSW2KPG = 0;
            val3BSW2GR = 0;
            val3BSW3KPG = 0;
            val3BSW3GR = 0;
            val3BS_KPG = 0;
            val3BS_GR = 0;
            val3BBW1KPG = 0;
            val3BBW1GR = 0;
            val3BBW2KPG = 0;
            val3BBW2GR = 0;
            val3BBW3KPG = 0;
            val3BBW3GR = 0;
            val3BB_KPG = 0;
            val3BB_GR = 0;
            val3BB2W1KPG = 0;
            val3BB2W1GR = 0;
            val3BB2W2KPG = 0;
            val3BB2W2GR = 0;
            val3BB2W3KPG = 0;
            val3BB2W3GR = 0;
            val3BB2_KPG = 0;
            val3BB2_GR = 0;
            val3_W1KPG = 0;
            val3_W1GR = 0;
            val3_W2KPG = 0;
            val3_W2GR = 0;
            val3_W3KPG = 0;
            val3_W3GR = 0;
            val3__KPG = 0;
            val3__GR = 0;
            valMkLainBRSBRW1KPG = 0;
            valMkLainBRSBRW1GR = 0;
            valMkLainBRSBRW2KPG = 0;
            valMkLainBRSBRW2GR = 0;
            valMkLainBRSBRW3KPG = 0;
            valMkLainBRSBRW3GR = 0;
            valMkLainBRSBR_KPG = 0;
            valMkLainBRSBR_GR = 0;
            valMkLainBRW1KPG = 0;
            valMkLainBRW1GR = 0;
            valMkLainBRW2KPG = 0;
            valMkLainBRW2GR = 0;
            valMkLainBRW3KPG = 0;
            valMkLainBRW3GR = 0;
            valMkLainBR_KPG = 0;
            valMkLainBR_GR = 0;
            valMkLainBSW1KPG = 0;
            valMkLainBSW1GR = 0;
            valMkLainBSW2KPG = 0;
            valMkLainBSW2GR = 0;
            valMkLainBSW3KPG = 0;
            valMkLainBSW3GR = 0;
            valMkLainBS_KPG = 0;
            valMkLainBS_GR = 0;
            valMkLainBBW1KPG = 0;
            valMkLainBBW1GR = 0;
            valMkLainBBW2KPG = 0;
            valMkLainBBW2GR = 0;
            valMkLainBBW3KPG = 0;
            valMkLainBBW3GR = 0;
            valMkLainBB_KPG = 0;
            valMkLainBB_GR = 0;
            valMkLainBB2W1KPG = 0;
            valMkLainBB2W1GR = 0;
            valMkLainBB2W2KPG = 0;
            valMkLainBB2W2GR = 0;
            valMkLainBB2W3KPG = 0;
            valMkLainBB2W3GR = 0;
            valMkLainBB2_KPG = 0;
            valMkLainBB2_GR = 0;
            valMkLain_W1KPG = 0;
            valMkLain_W1GR = 0;
            valMkLain_W2KPG = 0;
            valMkLain_W2GR = 0;
            valMkLain_W3KPG = 0;
            valMkLain_W3GR = 0;
            valMkLain__KPG = 0;
            valMkLain__GR = 0;
            valSgtgBRSBRW1KPG = 0;
            valSgtgBRSBRW1GR = 0;
            valSgtgBRSBRW2KPG = 0;
            valSgtgBRSBRW2GR = 0;
            valSgtgBRSBRW3KPG = 0;
            valSgtgBRSBRW3GR = 0;
            valSgtgBRSBR_KPG = 0;
            valSgtgBRSBR_GR = 0;
            valSgtgBRW1KPG = 0;
            valSgtgBRW1GR = 0;
            valSgtgBRW2KPG = 0;
            valSgtgBRW2GR = 0;
            valSgtgBRW3KPG = 0;
            valSgtgBRW3GR = 0;
            valSgtgBR_KPG = 0;
            valSgtgBR_GR = 0;
            valSgtgBSW1KPG = 0;
            valSgtgBSW1GR = 0;
            valSgtgBSW2KPG = 0;
            valSgtgBSW2GR = 0;
            valSgtgBSW3KPG = 0;
            valSgtgBSW3GR = 0;
            valSgtgBS_KPG = 0;
            valSgtgBS_GR = 0;
            valSgtgBBW1KPG = 0;
            valSgtgBBW1GR = 0;
            valSgtgBBW2KPG = 0;
            valSgtgBBW2GR = 0;
            valSgtgBBW3KPG = 0;
            valSgtgBBW3GR = 0;
            valSgtgBB_KPG = 0;
            valSgtgBB_GR = 0;
            valSgtgBB2W1KPG = 0;
            valSgtgBB2W1GR = 0;
            valSgtgBB2W2KPG = 0;
            valSgtgBB2W2GR = 0;
            valSgtgBB2W3KPG = 0;
            valSgtgBB2W3GR = 0;
            valSgtgBB2_KPG = 0;
            valSgtgBB2_GR = 0;
            valSgtg_W1KPG = 0;
            valSgtg_W1GR = 0;
            valSgtg_W2KPG = 0;
            valSgtg_W2GR = 0;
            valSgtg_W3KPG = 0;
            valSgtg_W3GR = 0;
            valSgtg__KPG = 0;
            valSgtg__GR = 0;
            valOvalBRSBRW1KPG = 0;
            valOvalBRSBRW1GR = 0;
            valOvalBRSBRW2KPG = 0;
            valOvalBRSBRW2GR = 0;
            valOvalBRSBRW3KPG = 0;
            valOvalBRSBRW3GR = 0;
            valOvalBRSBR_KPG = 0;
            valOvalBRSBR_GR = 0;
            valOvalBRW1KPG = 0;
            valOvalBRW1GR = 0;
            valOvalBRW2KPG = 0;
            valOvalBRW2GR = 0;
            valOvalBRW3KPG = 0;
            valOvalBRW3GR = 0;
            valOvalBR_KPG = 0;
            valOvalBR_GR = 0;
            valOvalBSW1KPG = 0;
            valOvalBSW1GR = 0;
            valOvalBSW2KPG = 0;
            valOvalBSW2GR = 0;
            valOvalBSW3KPG = 0;
            valOvalBSW3GR = 0;
            valOvalBS_KPG = 0;
            valOvalBS_GR = 0;
            valOvalBBW1KPG = 0;
            valOvalBBW1GR = 0;
            valOvalBBW2KPG = 0;
            valOvalBBW2GR = 0;
            valOvalBBW3KPG = 0;
            valOvalBBW3GR = 0;
            valOvalBB_KPG = 0;
            valOvalBB_GR = 0;
            valOvalBB2W1KPG = 0;
            valOvalBB2W1GR = 0;
            valOvalBB2W2KPG = 0;
            valOvalBB2W2GR = 0;
            valOvalBB2W3KPG = 0;
            valOvalBB2W3GR = 0;
            valOvalBB2_KPG = 0;
            valOvalBB2_GR = 0;
            valOval_W1KPG = 0;
            valOval_W1GR = 0;
            valOval_W2KPG = 0;
            valOval_W2GR = 0;
            valOval_W3KPG = 0;
            valOval_W3GR = 0;
            valOval__KPG = 0;
            valOval__GR = 0;
            valPchLbgBRSBRW1KPG = 0;
            valPchLbgBRSBRW1GR = 0;
            valPchLbgBRSBRW2KPG = 0;
            valPchLbgBRSBRW2GR = 0;
            valPchLbgBRSBRW3KPG = 0;
            valPchLbgBRSBRW3GR = 0;
            valPchLbgBRSBR_KPG = 0;
            valPchLbgBRSBR_GR = 0;
            valPchLbgBRW1KPG = 0;
            valPchLbgBRW1GR = 0;
            valPchLbgBRW2KPG = 0;
            valPchLbgBRW2GR = 0;
            valPchLbgBRW3KPG = 0;
            valPchLbgBRW3GR = 0;
            valPchLbgBR_KPG = 0;
            valPchLbgBR_GR = 0;
            valPchLbgBSW1KPG = 0;
            valPchLbgBSW1GR = 0;
            valPchLbgBSW2KPG = 0;
            valPchLbgBSW2GR = 0;
            valPchLbgBSW3KPG = 0;
            valPchLbgBSW3GR = 0;
            valPchLbgBS_KPG = 0;
            valPchLbgBS_GR = 0;
            valPchLbgBBW1KPG = 0;
            valPchLbgBBW1GR = 0;
            valPchLbgBBW2KPG = 0;
            valPchLbgBBW2GR = 0;
            valPchLbgBBW3KPG = 0;
            valPchLbgBBW3GR = 0;
            valPchLbgBB_KPG = 0;
            valPchLbgBB_GR = 0;
            valPchLbgBB2W1KPG = 0;
            valPchLbgBB2W1GR = 0;
            valPchLbgBB2W2KPG = 0;
            valPchLbgBB2W2GR = 0;
            valPchLbgBB2W3KPG = 0;
            valPchLbgBB2W3GR = 0;
            valPchLbgBB2_KPG = 0;
            valPchLbgBB2_GR = 0;
            valPchLbg_W1KPG = 0;
            valPchLbg_W1GR = 0;
            valPchLbg_W2KPG = 0;
            valPchLbg_W2GR = 0;
            valPchLbg_W3KPG = 0;
            valPchLbg_W3GR = 0;
            valPchLbg__KPG = 0;
            valPchLbg__GR = 0;
            valTotalW1KPG = 0;
            valTotalW1GR = 0;
            valTotalW2KPG = 0;
            valTotalW2GR = 0;
            valTotalW3KPG = 0;
            valTotalW3GR = 0;
            valTotal_KPG = 0;
            valTotal_GR = 0;
            valTotalTotalKPG = 0;
            valTotalTotalGR = 0;
            valTotal1BRSBRKPG = 0;
            valTotal1BRSBRGR = 0;
            valTotal1BRKPG = 0;
            valTotal1BRGR = 0;
            valTotal1BSKPG = 0;
            valTotal1BSGR = 0;
            valTotal1BBKPG = 0;
            valTotal1BBGR = 0;
            valTotal1BB2KPG = 0;
            valTotal1BB2GR = 0;
            valTotal1_KPG = 0;
            valTotal1_GR = 0;
            valTotal2BRSBRKPG = 0;
            valTotal2BRSBRGR = 0;
            valTotal2BRKPG = 0;
            valTotal2BRGR = 0;
            valTotal2BSKPG = 0;
            valTotal2BSGR = 0;
            valTotal2BBKPG = 0;
            valTotal2BBGR = 0;
            valTotal2BB2KPG = 0;
            valTotal2BB2GR = 0;
            valTotal2_KPG = 0;
            valTotal2_GR = 0;
            valTotal3BRSBRKPG = 0;
            valTotal3BRSBRGR = 0;
            valTotal3BRKPG = 0;
            valTotal3BRGR = 0;
            valTotal3BSKPG = 0;
            valTotal3BSGR = 0;
            valTotal3BBKPG = 0;
            valTotal3BBGR = 0;
            valTotal3BB2KPG = 0;
            valTotal3BB2GR = 0;
            valTotal3_KPG = 0;
            valTotal3_GR = 0;
            valTotalMkLainBRSBRKPG = 0;
            valTotalMkLainBRSBRGR = 0;
            valTotalMkLainBRKPG = 0;
            valTotalMkLainBRGR = 0;
            valTotalMkLainBSKPG = 0;
            valTotalMkLainBSGR = 0;
            valTotalMkLainBBKPG = 0;
            valTotalMkLainBBGR = 0;
            valTotalMkLainBB2KPG = 0;
            valTotalMkLainBB2GR = 0;
            valTotalMkLain_KPG = 0;
            valTotalMkLain_GR = 0;
            valTotalSgtgBRSBRKPG = 0;
            valTotalSgtgBRSBRGR = 0;
            valTotalSgtgBRKPG = 0;
            valTotalSgtgBRGR = 0;
            valTotalSgtgBSKPG = 0;
            valTotalSgtgBSGR = 0;
            valTotalSgtgBBKPG = 0;
            valTotalSgtgBBGR = 0;
            valTotalSgtgBB2KPG = 0;
            valTotalSgtgBB2GR = 0;
            valTotalSgtg_KPG = 0;
            valTotalSgtg_GR = 0;
            valTotalOvalBRSBRKPG = 0;
            valTotalOvalBRSBRGR = 0;
            valTotalOvalBRKPG = 0;
            valTotalOvalBRGR = 0;
            valTotalOvalBSKPG = 0;
            valTotalOvalBSGR = 0;
            valTotalOvalBBKPG = 0;
            valTotalOvalBBGR = 0;
            valTotalOvalBB2KPG = 0;
            valTotalOvalBB2GR = 0;
            valTotalOval_KPG = 0;
            valTotalOval_GR = 0;
            valTotalPchLbgBRSBRKPG = 0;
            valTotalPchLbgBRSBRGR = 0;
            valTotalPchLbgBRKPG = 0;
            valTotalPchLbgBRGR = 0;
            valTotalPchLbgBSKPG = 0;
            valTotalPchLbgBSGR = 0;
            valTotalPchLbgBBKPG = 0;
            valTotalPchLbgBBGR = 0;
            valTotalPchLbgBB2KPG = 0;
            valTotalPchLbgBB2GR = 0;
            valTotalPchLbg_KPG = 0;
            valTotalPchLbg_GR = 0;
            val_W1KPG = 0;
            val_W1GR = 0;
            val_W2KPG = 0;
            val_W2GR = 0;
            val_W3KPG = 0;
            val_W3GR = 0;
            val__KPG = 0;
            val__GR = 0;
            val_TotalKPG = 0;
            val_TotalGR = 0;
            int kpg_masuk = 0, gram_masuk = 0, kpg_lp = 0, gram_lp = 0, kpg_jual = 0, gram_keluar = 0, kpg_cmp = 0, gram_cmp = 0;
            int total_kpg_masuk = 0, total_gram_masuk = 0, total_kpg_lp = 0, total_gram_lp = 0, total_kpg_jual = 0, total_gram_keluar = 0, total_kpg_cmp = 0, total_gram_cmp = 0;
            int total_kpg_sisa = 0, total_gram_sisa = 0, total_gram_stok_waleta = 0, total_gram_stok_sub = 0, total_gram_stok_bp = 0, total_gram_stok_unworkable = 0;
            ResultSet rs_grading, rs_jual, rs_lp, rs_cmp;
            DefaultTableModel model = (DefaultTableModel) table_stok_per_grade.getModel();
            model.setRowCount(0);
            String filter_bentuk = "1";
            if (null == ComboBox_FilterBentuk.getSelectedItem().toString()) {
                filter_bentuk = "1";
            } else {
                switch (ComboBox_FilterBentuk.getSelectedItem().toString()) {
                    case "All":
                        filter_bentuk = "1";
                        break;
                    case "-":
                        filter_bentuk = "`jenis_bentuk` NOT LIKE '%Pecah%' AND `jenis_bentuk` NOT LIKE '%Lubang%' AND `jenis_bentuk` NOT LIKE '%segitiga%' AND `jenis_bentuk` NOT LIKE '%mangkok%' AND `jenis_bentuk` NOT LIKE '%oval%'";
                        break;
                    case "Pch / Lbg":
                        filter_bentuk = "(`jenis_bentuk` LIKE '%Pecah%' OR `jenis_bentuk` LIKE '%Lubang%')";
                        break;
                    default:
                        filter_bentuk = "`jenis_bentuk` LIKE '%" + ComboBox_FilterBentuk.getSelectedItem().toString() + "%'";
                        break;
                }
            }
            String filter_bulu = "";
            if ("All".equals(ComboBox_FilterBulu.getSelectedItem().toString())) {
                filter_bulu = "";
            } else {
                filter_bulu = " AND `jenis_bulu` = '" + ComboBox_FilterBulu.getSelectedItem().toString() + "'";
            }
            String filter_warna = "";
            if ("All".equals(ComboBox_FilterWarna.getSelectedItem().toString())) {
                filter_warna = "";
            } else {
                filter_warna = " AND `jenis_warna` = '" + ComboBox_FilterWarna.getSelectedItem().toString() + "'";
            }
            String filter_kategori = "";
            if ("All".equals(ComboBox_FilterKategori.getSelectedItem().toString())) {
                filter_kategori = "";
            } else {
                filter_kategori = " AND `kategori_proses` = '" + ComboBox_FilterKategori.getSelectedItem().toString() + "'";
            }
            sql = "SELECT * FROM `tb_grade_bahan_baku` WHERE " + filter_bentuk + filter_bulu + filter_warna + filter_kategori;
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[13];
            while (rs.next()) {
                try {
                    row[0] = rs.getString("kode_grade");
                    row[12] = rs.getString("jenis_bulu");
                    row[11] = rs.getString("kategori_proses");
                    String sql_grading = "SELECT SUM(`tb_grading_bahan_baku`.`jumlah_keping`) AS 'jumlah_keping', SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'total_berat' "
                            + "FROM `tb_grading_bahan_baku` "
                            + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                            + "WHERE `kode_grade` = '" + rs.getString("kode_grade") + "' "
                            + "AND `tgl_masuk`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                    rs_grading = Utility.db.getStatement().executeQuery(sql_grading);
                    if (rs_grading.next()) {
                        row[1] = rs_grading.getInt("jumlah_keping");
                        kpg_masuk = rs_grading.getInt("jumlah_keping");
                        total_kpg_masuk = total_kpg_masuk + rs_grading.getInt("jumlah_keping");
                        row[2] = rs_grading.getInt("total_berat");
                        gram_masuk = rs_grading.getInt("total_berat");
                        total_gram_masuk = total_gram_masuk + rs_grading.getInt("total_berat");
                    }
                    String sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' "
                            + "FROM `tb_laporan_produksi` "
                            + "WHERE `kode_grade` = '" + rs.getString("kode_grade") + "'  "
                            + "AND `tanggal_lp`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                    rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                    if (rs_lp.next()) {
                        row[3] = rs_lp.getInt("keping");
                        kpg_lp = rs_lp.getInt("keping");
                        total_kpg_lp = total_kpg_lp + rs_lp.getInt("keping");
                        row[4] = rs_lp.getInt("berat");
                        gram_lp = rs_lp.getInt("berat");
                        total_gram_lp = total_kpg_lp + rs_lp.getInt("berat");
                    }
                    String sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' "
                            + "FROM `tb_bahan_baku_keluar` "
                            + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran` "
                            + "WHERE `tb_bahan_baku_keluar`.`kode_grade` = '" + rs.getString("kode_grade") + "' "
                            + "AND `tgl_keluar`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                    rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                    if (rs_jual.next()) {
                        row[5] = rs_jual.getInt("keping");
                        kpg_jual = rs_jual.getInt("keping");
                        total_kpg_jual = total_kpg_jual + rs_jual.getInt("keping");
                        row[6] = rs_jual.getInt("berat");
                        gram_keluar = rs_jual.getInt("berat");
                        total_gram_keluar = total_gram_keluar + rs_jual.getInt("berat");
                    }
                    String sql_cmp = "SELECT SUM(`keping`) AS 'keping_cmp', SUM(`gram`) AS 'berat_cmp' FROM `tb_kartu_cmp_detail`\n"
                            + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                            + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                            + "WHERE `tb_grading_bahan_baku`.`kode_grade` = '" + rs.getString("kode_grade") + "' "
                            + "AND `tanggal`<='" + dateFormat.format(Date_filter1.getDate()) + "'";
                    rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                    if (rs_cmp.next()) {
                        row[7] = rs_cmp.getInt("keping_cmp");
                        kpg_cmp = rs_cmp.getInt("keping_cmp");
                        total_kpg_cmp = total_kpg_cmp + rs_cmp.getInt("keping_cmp");
                        row[8] = rs_cmp.getInt("berat_cmp");
                        gram_cmp = rs_cmp.getInt("berat_cmp");
                        total_gram_cmp = total_kpg_cmp + rs_cmp.getInt("berat_cmp");
                    }
                    int kpg_sisa = kpg_masuk - (kpg_lp + kpg_jual + kpg_cmp);
                    int gram_sisa = gram_masuk - (gram_lp + gram_keluar + gram_cmp);
                    total_kpg_sisa = total_kpg_sisa + kpg_sisa;
                    total_gram_sisa = total_gram_sisa + gram_sisa;
                    switch (rs.getString("kategori_proses")) {
                        case "WALETA":
                            total_gram_stok_waleta = total_gram_stok_waleta + gram_sisa;
                            break;
                        case "SUB":
                            total_gram_stok_sub = total_gram_stok_sub + gram_sisa;
                            break;
                        case "BP":
                            total_gram_stok_bp = total_gram_stok_bp + gram_sisa;
                            break;
                        case "JUAL":
                            total_gram_stok_unworkable = total_gram_stok_unworkable + gram_sisa;
                            break;
                        default:
                            break;
                    }
                    row[9] = kpg_sisa;
                    row[10] = gram_sisa;

                    decimalFormat.setGroupingUsed(true);
                    txt_kpg_masuk.setText(decimalFormat.format(total_kpg_masuk));
                    txt_kpg_LP.setText(decimalFormat.format(total_kpg_lp));
                    txt_kpg_jual.setText(decimalFormat.format(total_kpg_jual));
                    txt_kpg_sisa.setText(decimalFormat.format(total_kpg_sisa));
                    txt_berat_masuk.setText(decimalFormat.format(total_gram_masuk));
                    txt_berat_LP.setText(decimalFormat.format(total_gram_lp));
                    txt_berat_jual.setText(decimalFormat.format(total_gram_keluar));
                    txt_berat_sisa.setText(decimalFormat.format(total_gram_sisa));

                    txt_stok_waleta.setText(decimalFormat.format(total_gram_stok_waleta));
                    txt_stok_sub.setText(decimalFormat.format(total_gram_stok_sub));
                    txt_stok_bp.setText(decimalFormat.format(total_gram_stok_bp));
                    txt_stok_unworkable.setText(decimalFormat.format(total_gram_stok_unworkable));

                    valTotalTotalKPG += ((Integer) row[9]).longValue();
                    valTotalTotalGR += ((Integer) row[10]).longValue();
                    switch (rs.getString("jenis_bentuk")) {
                        case "Mangkok":
                            if (rs.getString("kode_grade").contains("1 ")) {
                                switch (rs.getString("jenis_bulu")) {
                                    case "Bulu Ringan Sekali/Bulu Ringan":
                                        valTotal1BRSBRKPG += ((Integer) row[9]).longValue();
                                        valTotal1BRSBRGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val1BRSBRW1KPG += ((Integer) row[9]).longValue();
                                                val1BRSBRW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("1BRSBRW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val1BRSBRW2KPG += ((Integer) row[9]).longValue();
                                                val1BRSBRW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val1BRSBRW3KPG += ((Integer) row[9]).longValue();
                                                val1BRSBRW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val1BRSBR_KPG += ((Integer) row[9]).longValue();
                                                val1BRSBR_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Ringan":
                                        valTotal1BRKPG += ((Integer) row[9]).longValue();
                                        valTotal1BRGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val1BRW1KPG += ((Integer) row[9]).longValue();
                                                val1BRW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("1BRW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val1BRW2KPG += ((Integer) row[9]).longValue();
                                                val1BRW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val1BRW3KPG += ((Integer) row[9]).longValue();
                                                val1BRW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val1BR_KPG += ((Integer) row[9]).longValue();
                                                val1BR_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Sedang":
                                        valTotal1BSKPG += ((Integer) row[9]).longValue();
                                        valTotal1BSGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val1BSW1KPG += ((Integer) row[9]).longValue();
                                                val1BSW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("1BSW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val1BSW2KPG += ((Integer) row[9]).longValue();
                                                val1BSW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val1BSW3KPG += ((Integer) row[9]).longValue();
                                                val1BSW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val1BS_KPG += ((Integer) row[9]).longValue();
                                                val1BS_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Berat":
                                        valTotal1BBKPG += ((Integer) row[9]).longValue();
                                        valTotal1BBGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val1BBW1KPG += ((Integer) row[9]).longValue();
                                                val1BBW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("1BBW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val1BBW2KPG += ((Integer) row[9]).longValue();
                                                val1BBW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val1BBW3KPG += ((Integer) row[9]).longValue();
                                                val1BBW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val1BB_KPG += ((Integer) row[9]).longValue();
                                                val1BB_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Berat Sekali":
                                        valTotal1BB2KPG += ((Integer) row[9]).longValue();
                                        valTotal1BB2GR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val1BB2W1KPG += ((Integer) row[9]).longValue();
                                                val1BB2W1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("1BB2W1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val1BB2W2KPG += ((Integer) row[9]).longValue();
                                                val1BB2W2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val1BB2W3KPG += ((Integer) row[9]).longValue();
                                                val1BB2W3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val1BB2_KPG += ((Integer) row[9]).longValue();
                                                val1BB2_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    default:
                                        valTotal1_KPG += ((Integer) row[9]).longValue();
                                        valTotal1_GR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val1_W1KPG += ((Integer) row[9]).longValue();
                                                val1_W1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("1-W1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val1_W2KPG += ((Integer) row[9]).longValue();
                                                val1_W2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val1_W3KPG += ((Integer) row[9]).longValue();
                                                val1_W3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val1__KPG += ((Integer) row[9]).longValue();
                                                val1__GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                }
                            } else if ((rs.getString("kode_grade").contains("2 ") && !rs.getString("kode_grade").contains("3 ")) || rs.getString("kode_grade").contains("I/II ")) {
                                switch (rs.getString("jenis_bulu")) {
                                    case "Bulu Ringan Sekali/Bulu Ringan":
                                        valTotal2BRSBRKPG += ((Integer) row[9]).longValue();
                                        valTotal2BRSBRGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val2BRSBRW1KPG += ((Integer) row[9]).longValue();
                                                val2BRSBRW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("2BRSBRW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val2BRSBRW2KPG += ((Integer) row[9]).longValue();
                                                val2BRSBRW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val2BRSBRW3KPG += ((Integer) row[9]).longValue();
                                                val2BRSBRW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val2BRSBR_KPG += ((Integer) row[9]).longValue();
                                                val2BRSBR_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Ringan":
                                        valTotal2BRKPG += ((Integer) row[9]).longValue();
                                        valTotal2BRGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val2BRW1KPG += ((Integer) row[9]).longValue();
                                                val2BRW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("2BRW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val2BRW2KPG += ((Integer) row[9]).longValue();
                                                val2BRW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val2BRW3KPG += ((Integer) row[9]).longValue();
                                                val2BRW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val2BR_KPG += ((Integer) row[9]).longValue();
                                                val2BR_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Sedang":
                                        valTotal2BSKPG += ((Integer) row[9]).longValue();
                                        valTotal2BSGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val2BSW1KPG += ((Integer) row[9]).longValue();
                                                val2BSW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("2BSW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val2BSW2KPG += ((Integer) row[9]).longValue();
                                                val2BSW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val2BSW3KPG += ((Integer) row[9]).longValue();
                                                val2BSW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val2BS_KPG += ((Integer) row[9]).longValue();
                                                val2BS_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Berat":
                                        valTotal2BBKPG += ((Integer) row[9]).longValue();
                                        valTotal2BBGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val2BBW1KPG += ((Integer) row[9]).longValue();
                                                val2BBW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("2BBW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val2BBW2KPG += ((Integer) row[9]).longValue();
                                                val2BBW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val2BBW3KPG += ((Integer) row[9]).longValue();
                                                val2BBW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val2BB_KPG += ((Integer) row[9]).longValue();
                                                val2BB_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Berat Sekali":
                                        valTotal2BB2KPG += ((Integer) row[9]).longValue();
                                        valTotal2BB2GR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val2BB2W1KPG += ((Integer) row[9]).longValue();
                                                val2BB2W1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("2BB2W1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val2BB2W2KPG += ((Integer) row[9]).longValue();
                                                val2BB2W2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val2BB2W3KPG += ((Integer) row[9]).longValue();
                                                val2BB2W3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val2BB2_KPG += ((Integer) row[9]).longValue();
                                                val2BB2_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    default:
                                        valTotal2_KPG += ((Integer) row[9]).longValue();
                                        valTotal2_GR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val2_W1KPG += ((Integer) row[9]).longValue();
                                                val2_W1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("2-W1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val2_W2KPG += ((Integer) row[9]).longValue();
                                                val2_W2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val2_W3KPG += ((Integer) row[9]).longValue();
                                                val2_W3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val2__KPG += ((Integer) row[9]).longValue();
                                                val2__GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                }
                            } else if (rs.getString("kode_grade").contains("3 ")) {
                                switch (rs.getString("jenis_bulu")) {
                                    case "Bulu Ringan Sekali/Bulu Ringan":
                                        valTotal3BRSBRKPG += ((Integer) row[9]).longValue();
                                        valTotal3BRSBRGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val3BRSBRW1KPG += ((Integer) row[9]).longValue();
                                                val3BRSBRW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("3BRSBRW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val3BRSBRW2KPG += ((Integer) row[9]).longValue();
                                                val3BRSBRW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val3BRSBRW3KPG += ((Integer) row[9]).longValue();
                                                val3BRSBRW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val3BRSBR_KPG += ((Integer) row[9]).longValue();
                                                val3BRSBR_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Ringan":
                                        valTotal3BRKPG += ((Integer) row[9]).longValue();
                                        valTotal3BRGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val3BRW1KPG += ((Integer) row[9]).longValue();
                                                val3BRW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("3BRW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val3BRW2KPG += ((Integer) row[9]).longValue();
                                                val3BRW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val3BRW3KPG += ((Integer) row[9]).longValue();
                                                val3BRW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val3BR_KPG += ((Integer) row[9]).longValue();
                                                val3BR_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Sedang":
                                        valTotal3BSKPG += ((Integer) row[9]).longValue();
                                        valTotal3BSGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val3BSW1KPG += ((Integer) row[9]).longValue();
                                                val3BSW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("3BSW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val3BSW2KPG += ((Integer) row[9]).longValue();
                                                val3BSW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val3BSW3KPG += ((Integer) row[9]).longValue();
                                                val3BSW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val3BS_KPG += ((Integer) row[9]).longValue();
                                                val3BS_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Berat":
                                        valTotal3BBKPG += ((Integer) row[9]).longValue();
                                        valTotal3BBGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val3BBW1KPG += ((Integer) row[9]).longValue();
                                                val3BBW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("3BBW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val3BBW2KPG += ((Integer) row[9]).longValue();
                                                val3BBW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val3BBW3KPG += ((Integer) row[9]).longValue();
                                                val3BBW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val3BB_KPG += ((Integer) row[9]).longValue();
                                                val3BB_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Berat Sekali":
                                        valTotal3BB2KPG += ((Integer) row[9]).longValue();
                                        valTotal3BB2GR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val3BB2W1KPG += ((Integer) row[9]).longValue();
                                                val3BB2W1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("3BB2W1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val3BB2W2KPG += ((Integer) row[9]).longValue();
                                                val3BB2W2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val3BB2W3KPG += ((Integer) row[9]).longValue();
                                                val3BB2W3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val3BB2_KPG += ((Integer) row[9]).longValue();
                                                val3BB2_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    default:
                                        valTotal3_KPG += ((Integer) row[9]).longValue();
                                        valTotal3_GR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                val3_W1KPG += ((Integer) row[9]).longValue();
                                                val3_W1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("3-W1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                val3_W2KPG += ((Integer) row[9]).longValue();
                                                val3_W2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                val3_W3KPG += ((Integer) row[9]).longValue();
                                                val3_W3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                val3__KPG += ((Integer) row[9]).longValue();
                                                val3__GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                }
                            } else {
                                switch (rs.getString("jenis_bulu")) {
                                    case "Bulu Ringan Sekali/Bulu Ringan":
                                        valTotalMkLainBRSBRKPG += ((Integer) row[9]).longValue();
                                        valTotalMkLainBRSBRGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                valMkLainBRSBRW1KPG += ((Integer) row[9]).longValue();
                                                valMkLainBRSBRW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("MkLainBRSBRW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                valMkLainBRSBRW2KPG += ((Integer) row[9]).longValue();
                                                valMkLainBRSBRW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                valMkLainBRSBRW3KPG += ((Integer) row[9]).longValue();
                                                valMkLainBRSBRW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                valMkLainBRSBR_KPG += ((Integer) row[9]).longValue();
                                                valMkLainBRSBR_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Ringan":
                                        valTotalMkLainBRKPG += ((Integer) row[9]).longValue();
                                        valTotalMkLainBRGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                valMkLainBRW1KPG += ((Integer) row[9]).longValue();
                                                valMkLainBRW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("MkLainBRW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                valMkLainBRW2KPG += ((Integer) row[9]).longValue();
                                                valMkLainBRW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                valMkLainBRW3KPG += ((Integer) row[9]).longValue();
                                                valMkLainBRW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                valMkLainBR_KPG += ((Integer) row[9]).longValue();
                                                valMkLainBR_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Sedang":
                                        valTotalMkLainBSKPG += ((Integer) row[9]).longValue();
                                        valTotalMkLainBSGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                valMkLainBSW1KPG += ((Integer) row[9]).longValue();
                                                valMkLainBSW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("MkLainBSW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                valMkLainBSW2KPG += ((Integer) row[9]).longValue();
                                                valMkLainBSW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                valMkLainBSW3KPG += ((Integer) row[9]).longValue();
                                                valMkLainBSW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                valMkLainBS_KPG += ((Integer) row[9]).longValue();
                                                valMkLainBS_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Berat":
                                        valTotalMkLainBBKPG += ((Integer) row[9]).longValue();
                                        valTotalMkLainBBGR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                valMkLainBBW1KPG += ((Integer) row[9]).longValue();
                                                valMkLainBBW1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("MkLainBBW1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                valMkLainBBW2KPG += ((Integer) row[9]).longValue();
                                                valMkLainBBW2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                valMkLainBBW3KPG += ((Integer) row[9]).longValue();
                                                valMkLainBBW3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                valMkLainBB_KPG += ((Integer) row[9]).longValue();
                                                valMkLainBB_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    case "Bulu Berat Sekali":
                                        valTotalMkLainBB2KPG += ((Integer) row[9]).longValue();
                                        valTotalMkLainBB2GR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                valMkLainBB2W1KPG += ((Integer) row[9]).longValue();
                                                valMkLainBB2W1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("MkLainBB2W1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                valMkLainBB2W2KPG += ((Integer) row[9]).longValue();
                                                valMkLainBB2W2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                valMkLainBB2W3KPG += ((Integer) row[9]).longValue();
                                                valMkLainBB2W3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                valMkLainBB2_KPG += ((Integer) row[9]).longValue();
                                                valMkLainBB2_GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                    default:
                                        valTotalMkLain_KPG += ((Integer) row[9]).longValue();
                                        valTotalMkLain_GR += ((Integer) row[10]).longValue();
                                        switch (rs.getString("jenis_warna")) {
                                            case "Warna 1":
                                                valMkLain_W1KPG += ((Integer) row[9]).longValue();
                                                valMkLain_W1GR += ((Integer) row[10]).longValue();
                                                valTotalW1KPG += ((Integer) row[9]).longValue();
                                                valTotalW1GR += ((Integer) row[10]).longValue();
//                                            System.out.println("MkLain-W1KPG=" + ((Integer) row[9]).longValue());
                                                break;
                                            case "Warna 2":
                                                valMkLain_W2KPG += ((Integer) row[9]).longValue();
                                                valMkLain_W2GR += ((Integer) row[10]).longValue();
                                                valTotalW2KPG += ((Integer) row[9]).longValue();
                                                valTotalW2GR += ((Integer) row[10]).longValue();
                                                break;
                                            case "Warna 3":
                                                valMkLain_W3KPG += ((Integer) row[9]).longValue();
                                                valMkLain_W3GR += ((Integer) row[10]).longValue();
                                                valTotalW3KPG += ((Integer) row[9]).longValue();
                                                valTotalW3GR += ((Integer) row[10]).longValue();
                                                break;
                                            default:
                                                valMkLain__KPG += ((Integer) row[9]).longValue();
                                                valMkLain__GR += ((Integer) row[10]).longValue();
                                                valTotal_KPG += ((Integer) row[9]).longValue();
                                                valTotal_GR += ((Integer) row[10]).longValue();
                                                break;
                                        }
                                        break;
                                }
                            }
                            break;
                        case "Segitiga":
                            switch (rs.getString("jenis_bulu")) {
                                case "Bulu Ringan Sekali/Bulu Ringan":
                                    valTotalSgtgBRSBRKPG += ((Integer) row[9]).longValue();
                                    valTotalSgtgBRSBRGR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valSgtgBRSBRW1KPG += ((Integer) row[9]).longValue();
                                            valSgtgBRSBRW1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("SgtgBRSBRW1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valSgtgBRSBRW2KPG += ((Integer) row[9]).longValue();
                                            valSgtgBRSBRW2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valSgtgBRSBRW3KPG += ((Integer) row[9]).longValue();
                                            valSgtgBRSBRW3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valSgtgBRSBR_KPG += ((Integer) row[9]).longValue();
                                            valSgtgBRSBR_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                case "Bulu Ringan":
                                    valTotalSgtgBRKPG += ((Integer) row[9]).longValue();
                                    valTotalSgtgBRGR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valSgtgBRW1KPG += ((Integer) row[9]).longValue();
                                            valSgtgBRW1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("SgtgBRW1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valSgtgBRW2KPG += ((Integer) row[9]).longValue();
                                            valSgtgBRW2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valSgtgBRW3KPG += ((Integer) row[9]).longValue();
                                            valSgtgBRW3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valSgtgBR_KPG += ((Integer) row[9]).longValue();
                                            valSgtgBR_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                case "Bulu Sedang":
                                    valTotalSgtgBSKPG += ((Integer) row[9]).longValue();
                                    valTotalSgtgBSGR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valSgtgBSW1KPG += ((Integer) row[9]).longValue();
                                            valSgtgBSW1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("SgtgBSW1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valSgtgBSW2KPG += ((Integer) row[9]).longValue();
                                            valSgtgBSW2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valSgtgBSW3KPG += ((Integer) row[9]).longValue();
                                            valSgtgBSW3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valSgtgBS_KPG += ((Integer) row[9]).longValue();
                                            valSgtgBS_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                case "Bulu Berat":
                                    valTotalSgtgBBKPG += ((Integer) row[9]).longValue();
                                    valTotalSgtgBBGR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valSgtgBBW1KPG += ((Integer) row[9]).longValue();
                                            valSgtgBBW1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("SgtgBBW1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valSgtgBBW2KPG += ((Integer) row[9]).longValue();
                                            valSgtgBBW2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valSgtgBBW3KPG += ((Integer) row[9]).longValue();
                                            valSgtgBBW3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valSgtgBB_KPG += ((Integer) row[9]).longValue();
                                            valSgtgBB_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                case "Bulu Berat Sekali":
                                    valTotalSgtgBB2KPG += ((Integer) row[9]).longValue();
                                    valTotalSgtgBB2GR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valSgtgBB2W1KPG += ((Integer) row[9]).longValue();
                                            valSgtgBB2W1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("SgtgBB2W1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valSgtgBB2W2KPG += ((Integer) row[9]).longValue();
                                            valSgtgBB2W2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valSgtgBB2W3KPG += ((Integer) row[9]).longValue();
                                            valSgtgBB2W3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valSgtgBB2_KPG += ((Integer) row[9]).longValue();
                                            valSgtgBB2_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                default:
                                    valTotalSgtg_KPG += ((Integer) row[9]).longValue();
                                    valTotalSgtg_GR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valSgtg_W1KPG += ((Integer) row[9]).longValue();
                                            valSgtg_W1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("Sgtg-W1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valSgtg_W2KPG += ((Integer) row[9]).longValue();
                                            valSgtg_W2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valSgtg_W3KPG += ((Integer) row[9]).longValue();
                                            valSgtg_W3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valSgtg__KPG += ((Integer) row[9]).longValue();
                                            valSgtg__GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                            }
                            break;
                        case "Oval":
                            switch (rs.getString("jenis_bulu")) {
                                case "Bulu Ringan Sekali/Bulu Ringan":
                                    valTotalOvalBRSBRKPG += ((Integer) row[9]).longValue();
                                    valTotalOvalBRSBRGR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valOvalBRSBRW1KPG += ((Integer) row[9]).longValue();
                                            valOvalBRSBRW1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("OvalBRSBRW1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valOvalBRSBRW2KPG += ((Integer) row[9]).longValue();
                                            valOvalBRSBRW2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valOvalBRSBRW3KPG += ((Integer) row[9]).longValue();
                                            valOvalBRSBRW3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valOvalBRSBR_KPG += ((Integer) row[9]).longValue();
                                            valOvalBRSBR_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                case "Bulu Ringan":
                                    valTotalOvalBRKPG += ((Integer) row[9]).longValue();
                                    valTotalOvalBRGR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valOvalBRW1KPG += ((Integer) row[9]).longValue();
                                            valOvalBRW1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("OvalBRW1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valOvalBRW2KPG += ((Integer) row[9]).longValue();
                                            valOvalBRW2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valOvalBRW3KPG += ((Integer) row[9]).longValue();
                                            valOvalBRW3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valOvalBR_KPG += ((Integer) row[9]).longValue();
                                            valOvalBR_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                case "Bulu Sedang":
                                    valTotalOvalBSKPG += ((Integer) row[9]).longValue();
                                    valTotalOvalBSGR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valOvalBSW1KPG += ((Integer) row[9]).longValue();
                                            valOvalBSW1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("OvalBSW1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valOvalBSW2KPG += ((Integer) row[9]).longValue();
                                            valOvalBSW2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valOvalBSW3KPG += ((Integer) row[9]).longValue();
                                            valOvalBSW3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valOvalBS_KPG += ((Integer) row[9]).longValue();
                                            valOvalBS_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                case "Bulu Berat":
                                    valTotalOvalBBKPG += ((Integer) row[9]).longValue();
                                    valTotalOvalBBGR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valOvalBBW1KPG += ((Integer) row[9]).longValue();
                                            valOvalBBW1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("OvalBBW1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valOvalBBW2KPG += ((Integer) row[9]).longValue();
                                            valOvalBBW2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valOvalBBW3KPG += ((Integer) row[9]).longValue();
                                            valOvalBBW3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valOvalBB_KPG += ((Integer) row[9]).longValue();
                                            valOvalBB_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                case "Bulu Berat Sekali":
                                    valTotalOvalBB2KPG += ((Integer) row[9]).longValue();
                                    valTotalOvalBB2GR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valOvalBB2W1KPG += ((Integer) row[9]).longValue();
                                            valOvalBB2W1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("OvalBB2W1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valOvalBB2W2KPG += ((Integer) row[9]).longValue();
                                            valOvalBB2W2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valOvalBB2W3KPG += ((Integer) row[9]).longValue();
                                            valOvalBB2W3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valOvalBB2_KPG += ((Integer) row[9]).longValue();
                                            valOvalBB2_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                default:
                                    valTotalOval_KPG += ((Integer) row[9]).longValue();
                                    valTotalOval_GR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valOval_W1KPG += ((Integer) row[9]).longValue();
                                            valOval_W1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("Oval-W1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valOval_W2KPG += ((Integer) row[9]).longValue();
                                            valOval_W2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valOval_W3KPG += ((Integer) row[9]).longValue();
                                            valOval_W3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valOval__KPG += ((Integer) row[9]).longValue();
                                            valOval__GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                            }
                            break;
                        case "Pecah":
                        case "Lubang":
                            switch (rs.getString("jenis_bulu")) {
                                case "Bulu Ringan Sekali/Bulu Ringan":
                                    valTotalPchLbgBRSBRKPG += ((Integer) row[9]).longValue();
                                    valTotalPchLbgBRSBRGR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valPchLbgBRSBRW1KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBRSBRW1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("PchLbgBRSBRW1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valPchLbgBRSBRW2KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBRSBRW2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valPchLbgBRSBRW3KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBRSBRW3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valPchLbgBRSBR_KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBRSBR_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                case "Bulu Ringan":
                                    valTotalPchLbgBRKPG += ((Integer) row[9]).longValue();
                                    valTotalPchLbgBRGR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valPchLbgBRW1KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBRW1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("PchLbgBRW1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valPchLbgBRW2KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBRW2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valPchLbgBRW3KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBRW3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valPchLbgBR_KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBR_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                case "Bulu Sedang":
                                    valTotalPchLbgBSKPG += ((Integer) row[9]).longValue();
                                    valTotalPchLbgBSGR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valPchLbgBSW1KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBSW1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("PchLbgBSW1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valPchLbgBSW2KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBSW2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valPchLbgBSW3KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBSW3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valPchLbgBS_KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBS_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                case "Bulu Berat":
                                    valTotalPchLbgBBKPG += ((Integer) row[9]).longValue();
                                    valTotalPchLbgBBGR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valPchLbgBBW1KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBBW1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("PchLbgBBW1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valPchLbgBBW2KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBBW2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valPchLbgBBW3KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBBW3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valPchLbgBB_KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBB_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                case "Bulu Berat Sekali":
                                    valTotalPchLbgBB2KPG += ((Integer) row[9]).longValue();
                                    valTotalPchLbgBB2GR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valPchLbgBB2W1KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBB2W1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("PchLbgBB2W1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valPchLbgBB2W2KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBB2W2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valPchLbgBB2W3KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBB2W3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valPchLbgBB2_KPG += ((Integer) row[9]).longValue();
                                            valPchLbgBB2_GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                                default:
                                    valTotalPchLbg_KPG += ((Integer) row[9]).longValue();
                                    valTotalPchLbg_GR += ((Integer) row[10]).longValue();
                                    switch (rs.getString("jenis_warna")) {
                                        case "Warna 1":
                                            valPchLbg_W1KPG += ((Integer) row[9]).longValue();
                                            valPchLbg_W1GR += ((Integer) row[10]).longValue();
                                            valTotalW1KPG += ((Integer) row[9]).longValue();
                                            valTotalW1GR += ((Integer) row[10]).longValue();
//                                        System.out.println("PchLbg-W1KPG=" + ((Integer) row[9]).longValue());
                                            break;
                                        case "Warna 2":
                                            valPchLbg_W2KPG += ((Integer) row[9]).longValue();
                                            valPchLbg_W2GR += ((Integer) row[10]).longValue();
                                            valTotalW2KPG += ((Integer) row[9]).longValue();
                                            valTotalW2GR += ((Integer) row[10]).longValue();
                                            break;
                                        case "Warna 3":
                                            valPchLbg_W3KPG += ((Integer) row[9]).longValue();
                                            valPchLbg_W3GR += ((Integer) row[10]).longValue();
                                            valTotalW3KPG += ((Integer) row[9]).longValue();
                                            valTotalW3GR += ((Integer) row[10]).longValue();
                                            break;
                                        default:
                                            valPchLbg__KPG += ((Integer) row[9]).longValue();
                                            valPchLbg__GR += ((Integer) row[10]).longValue();
                                            valTotal_KPG += ((Integer) row[9]).longValue();
                                            valTotal_GR += ((Integer) row[10]).longValue();
                                            break;
                                    }
                                    break;
                            }
                            break;
                        default:
                            val_TotalKPG += ((Integer) row[9]).longValue();
                            val_TotalGR += ((Integer) row[10]).longValue();
                            switch (rs.getString("jenis_warna")) {
                                case "Warna 1":
                                    val_W1KPG += ((Integer) row[9]).longValue();
                                    val_W1GR += ((Integer) row[10]).longValue();
                                    valTotalW1KPG += ((Integer) row[9]).longValue();
                                    valTotalW1GR += ((Integer) row[10]).longValue();
//                                System.out.println("-W1KPG=" + ((Integer) row[9]).longValue());
                                    break;
                                case "Warna 2":
                                    val_W2KPG += ((Integer) row[9]).longValue();
                                    val_W2GR += ((Integer) row[10]).longValue();
                                    valTotalW2KPG += ((Integer) row[9]).longValue();
                                    valTotalW2GR += ((Integer) row[10]).longValue();
                                    break;
                                case "Warna 3":
                                    val_W3KPG += ((Integer) row[9]).longValue();
                                    val_W3GR += ((Integer) row[10]).longValue();
                                    valTotalW3KPG += ((Integer) row[9]).longValue();
                                    valTotalW3GR += ((Integer) row[10]).longValue();
                                    break;
                                default:
                                    val__KPG += ((Integer) row[9]).longValue();
                                    val__GR += ((Integer) row[10]).longValue();
                                    valTotal_KPG += ((Integer) row[9]).longValue();
                                    valTotal_GR += ((Integer) row[10]).longValue();
                                    break;
                            }
                            break;
                    }

                    model.addRow(row);
                    ColumnsAutoSizer.sizeColumnsToFit(table_stok_per_grade);
                } catch (SQLException ex) {
                    Logger.getLogger(JDialog_LaporanBaku_perGrade.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_TabelKartuMasuk() {
        try {
            int total_kpg = 0, total_gram = 0;
            DefaultTableModel model = (DefaultTableModel) tabel_KartuMasuk.getModel();
            model.setRowCount(0);
            sql = "SELECT `no_kartu_waleta`, `kode_grade`, `jumlah_keping`, `total_berat` FROM `tb_grading_bahan_baku` WHERE `kode_grade` = '" + label_grade.getText() + "'";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] row = new Object[4];
            while (rs.next()) {
                row[0] = rs.getString("no_kartu_waleta");
                row[1] = null;
                row[2] = rs.getInt("jumlah_keping");
                row[3] = rs.getInt("total_berat");
                model.addRow(row);
                total_kpg = total_kpg + rs.getInt("jumlah_keping");
                total_gram = total_gram + rs.getInt("total_berat");
            }
//            label_total_data_box.setText(Integer.toString(tabel_detail_box.getRowCount()));
//            label_total_keping_box.setText(Integer.toString(total_kpg));
//            label_total_gram_box.setText(Integer.toString(total_gram));
            ColumnsAutoSizer.sizeColumnsToFit(tabel_KartuMasuk);
        } catch (SQLException ex) {
            Logger.getLogger(JDialog_LaporanBaku_perGrade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refresh_TabelKetahanan() {
        decimalFormat.setMaximumFractionDigits(0);
        int kpg_masuk = 0, gram_masuk = 0, kpg_lp = 0, gram_lp = 0, kpg_jual = 0, gram_keluar = 0, kpg_cmp = 0, gram_cmp = 0;
        float stok_waleta = 0, stok_sub = 0;
        float stok_BRS_WLT = 0, stok_BR_WLT = 0, stok_BS_WLT = 0, stok_BuluLain_WLT = 0;
        ResultSet rs_grading, rs_jual, rs_lp, rs_cmp;
        try {
            DefaultTableModel model_waleta = (DefaultTableModel) table_stok_for_waleta.getModel();
            model_waleta.setRowCount(0);
            DefaultTableModel model_sub = (DefaultTableModel) table_stok_for_sub.getModel();
            model_sub.setRowCount(0);
            sql = "SELECT `kode_grade`, `jenis_bentuk`, `jenis_bulu`, `kategori_proses` FROM `tb_grade_bahan_baku` "
                    + "WHERE `kategori_proses` IN ('WALETA', 'SUB') GROUP BY `jenis_bentuk`, `jenis_bulu`, `kategori_proses`";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[4];
            while (rs.next()) {
                String sql_grading = "SELECT SUM(`tb_grading_bahan_baku`.`jumlah_keping`) AS 'jumlah_keping', SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'total_berat' \n"
                        + "FROM `tb_grading_bahan_baku` \n"
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`\n"
                        + "WHERE `tb_grade_bahan_baku`.`jenis_bentuk` = '" + rs.getString("jenis_bentuk") + "' \n"
//                        + "AND `tb_bahan_baku_masuk`.`last_stok` > 0 \n"
                        + "AND `tb_grade_bahan_baku`.`jenis_bulu` = '" + rs.getString("jenis_bulu") + "' \n"
                        + "AND `tb_grade_bahan_baku`.`kategori_proses` = '" + rs.getString("kategori_proses") + "' \n"
                        + "AND `tgl_masuk`<='" + dateFormat.format(Date_filter2.getDate()) + "'";
                rs_grading = Utility.db.getStatement().executeQuery(sql_grading);
                if (rs_grading.next()) {
                    kpg_masuk = rs_grading.getInt("jumlah_keping");
                    gram_masuk = rs_grading.getInt("total_berat");
                }
                String sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' "
                        + "FROM `tb_laporan_produksi` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_laporan_produksi`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_laporan_produksi`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "WHERE `tb_grade_bahan_baku`.`jenis_bentuk` = '" + rs.getString("jenis_bentuk") + "' "
//                        + "AND `tb_bahan_baku_masuk`.`last_stok` > 0 "
                        + "AND `tb_grade_bahan_baku`.`jenis_bulu` = '" + rs.getString("jenis_bulu") + "' "
                        + "AND `tb_grade_bahan_baku`.`kategori_proses` = '" + rs.getString("kategori_proses") + "' "
                        + "AND `tanggal_lp`<='" + dateFormat.format(Date_filter2.getDate()) + "'";
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    kpg_lp = rs_lp.getInt("keping");
                    gram_lp = rs_lp.getInt("berat");
                }
                String sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' "
                        + "FROM `tb_bahan_baku_keluar` "
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran` "
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_bahan_baku_keluar`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_bahan_baku_keluar`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "WHERE `tb_grade_bahan_baku`.`jenis_bentuk` = '" + rs.getString("jenis_bentuk") + "' "
//                        + "AND `tb_bahan_baku_masuk`.`last_stok` > 0 "
                        + "AND `tb_grade_bahan_baku`.`jenis_bulu` = '" + rs.getString("jenis_bulu") + "' "
                        + "AND `tb_grade_bahan_baku`.`kategori_proses` = '" + rs.getString("kategori_proses") + "' "
                        + "AND `tgl_keluar`<='" + dateFormat.format(Date_filter2.getDate()) + "'";
                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    kpg_jual = rs_jual.getInt("keping");
                    gram_keluar = rs_jual.getInt("berat");
                }
                String sql_cmp = "SELECT SUM(`keping`) AS 'keping_cmp', SUM(`gram`) AS 'berat_cmp' "
                        + "FROM `tb_kartu_cmp_detail`\n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "LEFT JOIN `tb_grade_bahan_baku` ON `tb_grading_bahan_baku`.`kode_grade` = `tb_grade_bahan_baku`.`kode_grade`"
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "WHERE `tb_grade_bahan_baku`.`jenis_bentuk` = '" + rs.getString("jenis_bentuk") + "' "
//                        + "AND `tb_bahan_baku_masuk`.`last_stok` > 0 "
                        + "AND `tb_grade_bahan_baku`.`jenis_bulu` = '" + rs.getString("jenis_bulu") + "' "
                        + "AND `tb_grade_bahan_baku`.`kategori_proses` = '" + rs.getString("kategori_proses") + "' "
                        + "AND `tanggal`<='" + dateFormat.format(Date_filter2.getDate()) + "'";
                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    kpg_cmp = rs_cmp.getInt("keping_cmp");
                    gram_cmp = rs_cmp.getInt("berat_cmp");
                }

                float kpg_sisa = kpg_masuk - (kpg_lp + kpg_jual + kpg_cmp);
                float gram_sisa = gram_masuk - (gram_lp + gram_keluar + gram_cmp);

                row[0] = rs.getString("jenis_bentuk");
                row[1] = rs.getString("jenis_bulu");
                row[2] = kpg_sisa;
                row[3] = gram_sisa;
                switch (rs.getString("kategori_proses")) {
                    case "WALETA":
                        switch (rs.getString("jenis_bulu")) {
                            case "Bulu Ringan Sekali/Bulu Ringan":
                                stok_BRS_WLT = stok_BRS_WLT + gram_sisa;
                                break;
                            case "Bulu Ringan":
                                stok_BR_WLT = stok_BR_WLT + gram_sisa;
                                break;
                            case "Bulu Sedang":
                                stok_BS_WLT = stok_BS_WLT + gram_sisa;
                                break;
                            default:
                                stok_BuluLain_WLT = stok_BuluLain_WLT + gram_sisa;
                                break;
                        }
                        stok_waleta = stok_waleta + gram_sisa;
                        model_waleta.addRow(row);
                        break;
                    case "SUB":
                        stok_sub = stok_sub + gram_sisa;
                        model_sub.addRow(row);
                        break;
                    default:
                        break;
                }
            }
            DefaultTableModel model = (DefaultTableModel) table_ketahanan_stok_waleta.getModel();
            model.setRowCount(0);
            sql = "SELECT COUNT(`tb_karyawan`.`id_pegawai`) AS 'borong_cabut' "
                    + "FROM `tb_karyawan` "
                    + "LEFT JOIN `tb_grup_cabut` ON `tb_karyawan`.`id_pegawai` = `tb_grup_cabut`.`id_pegawai` "
                    + "LEFT JOIN `tb_bagian` ON `tb_karyawan`.`kode_bagian` = `tb_bagian`.`kode_bagian` "
                    + "WHERE `status` = 'IN' AND `nama_bagian` LIKE '%CABUT-BORONG%'";
            rs = Utility.db.getStatement().executeQuery(sql);
            int jumlah_cabut_borong = 0;
            if (rs.next()) {
                jumlah_cabut_borong = rs.getInt("borong_cabut");
            }

            float ketahanan_BRS = stok_BRS_WLT / (jumlah_cabut_borong * 20 * 8);
            float ketahanan_BR = stok_BR_WLT / (jumlah_cabut_borong * 20 * 8);
            float ketahanan_BS = stok_BS_WLT / (jumlah_cabut_borong * 13 * 8);
            float ketahanan_Other = stok_BuluLain_WLT / (jumlah_cabut_borong * 16 * 8);
            decimalFormat.setMaximumFractionDigits(2);
            model.addRow(new Object[]{"BRS/BR", stok_BRS_WLT, jumlah_cabut_borong, 20, 8, (jumlah_cabut_borong * 20 * 8), decimalFormat.format(ketahanan_BRS)});
            model.addRow(new Object[]{"BR", stok_BR_WLT, jumlah_cabut_borong, 20, 8, (jumlah_cabut_borong * 20 * 8), decimalFormat.format(ketahanan_BR)});
            model.addRow(new Object[]{"BS", stok_BS_WLT, jumlah_cabut_borong, 13, 8, (jumlah_cabut_borong * 13 * 8), decimalFormat.format(ketahanan_BS)});
            model.addRow(new Object[]{"Other", stok_BuluLain_WLT, jumlah_cabut_borong, 16, 8, (jumlah_cabut_borong * 16 * 8), decimalFormat.format(ketahanan_Other)});
            model.addRow(new Object[]{"TOTAL", (stok_BRS_WLT + stok_BR_WLT + stok_BS_WLT + stok_BuluLain_WLT), null, null, null, null, decimalFormat.format(ketahanan_BRS + ketahanan_BR + ketahanan_BS + ketahanan_Other)});

            float ketahanan_waleta = ketahanan_BRS + ketahanan_BR + ketahanan_BS + ketahanan_Other;
            Date END_WLT = Utility.addDaysSkippingSundays(new Date(), Math.round(ketahanan_waleta));
            txt_tanggal_end_waleta.setText(new SimpleDateFormat("dd MMMM yyyy").format(END_WLT));
            ColumnsAutoSizer.sizeColumnsToFit(table_ketahanan_stok_waleta);
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_waleta);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            Date result = cal.getTime();
            sql = "SELECT AVG(`jumlah_per_hari`) AS 'avg' FROM \n"
                    + "(SELECT SUM(`berat_basah`) AS 'jumlah_per_hari' "
                    + "FROM `tb_laporan_produksi` \n"
                    + "WHERE "
                    + "LENGTH(`ruangan`) = 5 "
                    + "AND `tanggal_lp` BETWEEN '" + dateFormat.format(result) + "' AND '" + dateFormat.format(today) + "'\n"
                    + "GROUP BY `tanggal_lp`) AS T";
            rs = Utility.db.getStatement().executeQuery(sql);
            float rata2_pengeluaran_sub = 0;
            if (rs.next()) {
                rata2_pengeluaran_sub = rs.getInt("avg");
                txt_pengeluaran_sub.setText(decimalFormat.format(rata2_pengeluaran_sub));
            }
            float ketahanan_sub = stok_sub / rata2_pengeluaran_sub;
            txt_stok_sub1.setText(decimalFormat.format(stok_sub));
            txt_ketahanan_sub.setText(decimalFormat.format(ketahanan_sub));
            Date END_SUB = Utility.addDaysSkippingSundays(new Date(), Math.round(ketahanan_sub));
            txt_tanggal_end_sub.setText(new SimpleDateFormat("dd MMMM yyyy").format(END_SUB));
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_sub);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Refresh_BP_Unworkable() {
        try {
            ResultSet rs_grading, rs_jual, rs_lp, rs_cmp;
            int kpg_masuk = 0, gram_masuk = 0, kpg_lp = 0, gram_lp = 0, kpg_jual = 0, gram_keluar = 0, kpg_cmp = 0, gram_cmp = 0;
            float stok_bp = 0, stok_jual = 0;
            DefaultTableModel model_bp = (DefaultTableModel) table_stok_for_bp.getModel();
            model_bp.setRowCount(0);
            DefaultTableModel model_jual = (DefaultTableModel) table_stok_for_jual.getModel();
            model_jual.setRowCount(0);
            sql = "SELECT `kode_grade`, `jenis_bentuk`, `jenis_bulu`, `kategori_proses` FROM `tb_grade_bahan_baku` WHERE 1";
            pst = Utility.db.getConnection().prepareStatement(sql);
            rs = pst.executeQuery();
            Object[] row = new Object[4];
            while (rs.next()) {
                String sql_grading = "SELECT SUM(`tb_grading_bahan_baku`.`jumlah_keping`) AS 'jumlah_keping', SUM(`tb_grading_bahan_baku`.`total_berat`) AS 'total_berat' "
                        + "FROM `tb_grading_bahan_baku` "
                        + "LEFT JOIN `tb_bahan_baku_masuk` ON `tb_grading_bahan_baku`.`no_kartu_waleta` = `tb_bahan_baku_masuk`.`no_kartu_waleta`"
                        + "WHERE `kode_grade` = '" + rs.getString("kode_grade") + "' "
                        + "AND `tgl_masuk`<='" + dateFormat.format(Date_filter2.getDate()) + "'";
                rs_grading = Utility.db.getStatement().executeQuery(sql_grading);
                if (rs_grading.next()) {
                    kpg_masuk = rs_grading.getInt("jumlah_keping");
                    gram_masuk = rs_grading.getInt("total_berat");
                }
                String sql_lp = "SELECT SUM(`jumlah_keping`) AS 'keping', SUM(`berat_basah`) AS 'berat' "
                        + "FROM `tb_laporan_produksi` "
                        + "WHERE `kode_grade` = '" + rs.getString("kode_grade") + "'  "
                        + "AND `tanggal_lp`<='" + dateFormat.format(Date_filter2.getDate()) + "'";
                rs_lp = Utility.db.getStatement().executeQuery(sql_lp);
                if (rs_lp.next()) {
                    kpg_lp = rs_lp.getInt("keping");
                    gram_lp = rs_lp.getInt("berat");
                }
                String sql_jual = "SELECT SUM(`total_keping_keluar`) AS 'keping', SUM(`total_berat_keluar`) AS 'berat' "
                        + "FROM `tb_bahan_baku_keluar` "
                        + "LEFT JOIN `tb_bahan_baku_keluar1` ON `tb_bahan_baku_keluar`.`kode_pengeluaran` = `tb_bahan_baku_keluar1`.`kode_pengeluaran` "
                        + "WHERE `tb_bahan_baku_keluar`.`kode_grade` = '" + rs.getString("kode_grade") + "'  "
                        + "AND `tgl_keluar`<='" + dateFormat.format(Date_filter2.getDate()) + "'";
                rs_jual = Utility.db.getStatement().executeQuery(sql_jual);
                if (rs_jual.next()) {
                    kpg_jual = rs_jual.getInt("keping");
                    gram_keluar = rs_jual.getInt("berat");
                }
                String sql_cmp = "SELECT SUM(`keping`) AS 'keping_cmp', SUM(`gram`) AS 'berat_cmp' "
                        + "FROM `tb_kartu_cmp_detail`\n"
                        + "LEFT JOIN `tb_kartu_cmp` ON `tb_kartu_cmp_detail`.`kode_kartu_cmp` = `tb_kartu_cmp`.`kode_kartu_cmp`"
                        + "LEFT JOIN `tb_grading_bahan_baku` ON `tb_kartu_cmp_detail`.`no_grading` = `tb_grading_bahan_baku`.`no_grading`\n"
                        + "WHERE `tb_grading_bahan_baku`.`kode_grade` = '" + rs.getString("kode_grade") + "' "
                        + "AND `tanggal`<='" + dateFormat.format(Date_filter2.getDate()) + "'";
                rs_cmp = Utility.db.getStatement().executeQuery(sql_cmp);
                if (rs_cmp.next()) {
                    kpg_cmp = rs_cmp.getInt("keping_cmp");
                    gram_cmp = rs_cmp.getInt("berat_cmp");
                }
                float kpg_sisa = kpg_masuk - (kpg_lp + kpg_jual + kpg_cmp);
                float gram_sisa = gram_masuk - (gram_lp + gram_keluar + gram_cmp);

                row[0] = rs.getString("kode_grade");
                row[1] = kpg_sisa;
                row[2] = gram_sisa;
                switch (rs.getString("kategori_proses")) {
                    case "BP":
                        stok_bp = stok_bp + gram_sisa;
                        model_bp.addRow(row);
                        break;
                    case "JUAL":
                        stok_jual = stok_jual + gram_sisa;
                        model_jual.addRow(row);
                        break;
                    default:
                        break;
                }
            }
//            txt_stok_waleta1.setText(decimalFormat.format(stok_waleta));
            txt_stok_bp1.setText(decimalFormat.format(stok_bp));
            txt_stok_unworkable1.setText(decimalFormat.format(stok_jual));
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_bp);
            ColumnsAutoSizer.sizeColumnsToFit(table_stok_for_jual);
        } catch (SQLException ex) {
            Logger.getLogger(JPanel_DataBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void Refresh_grading_on_proses() {
        try {
            DefaultTableModel model_on_proses = (DefaultTableModel) table_on_proses.getModel();
            model_on_proses.setRowCount(0);
            int total_berat_onproses = 0;
            sql = "SELECT `no_kartu_waleta`, `tgl_masuk`, `tb_supplier`.`nama_supplier`, `berat_awal`, DATEDIFF(CURRENT_DATE, `tgl_masuk`) AS 'hari' "
                    + "FROM `tb_bahan_baku_masuk` "
                    + "LEFT JOIN `tb_supplier` ON `tb_bahan_baku_masuk`.`kode_supplier` = `tb_supplier`.`kode_supplier` "
                    + "WHERE `tgl_timbang` IS NULL "
                    + "AND `berat_awal` > 0 ORDER BY `hari` DESC";
            rs = Utility.db.getStatement().executeQuery(sql);
            Object[] baris = new Object[6];
            int no = 0;
            while (rs.next()) {
                no++;
                baris[0] = no;
                baris[1] = rs.getString("no_kartu_waleta");
                baris[2] = rs.getString("tgl_masuk");
                baris[3] = rs.getString("nama_supplier");
                baris[4] = decimalFormat.format(rs.getFloat("berat_awal"));
                baris[5] = rs.getInt("hari");
                total_berat_onproses = total_berat_onproses + rs.getInt("berat_awal");
                model_on_proses.addRow(baris);
            }
            txt_total_proses_grading.setText(decimalFormat.format(total_berat_onproses));
            table_on_proses.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                    if (column == 4) {
                    if ((int) table_on_proses.getValueAt(row, 5) > 7) {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) table_on_proses.getValueAt(row, 5) > 6) {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.YELLOW);
                            comp.setForeground(table_on_proses.getForeground());
                        }
                    } else if ((int) table_on_proses.getValueAt(row, 5) <= 6) {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.green);
                            comp.setForeground(table_on_proses.getForeground());
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(table_on_proses.getBackground());
                            comp.setForeground(table_on_proses.getForeground());
                        }
                    }
                    return comp;
                }
            });
            table_on_proses.repaint();
            table_on_proses.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if ((int) table_on_proses.getValueAt(row, 5) > 7) {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.red);
                            comp.setForeground(Color.WHITE);
                        }
                    } else if ((int) table_on_proses.getValueAt(row, 5) > 6) {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.YELLOW);
                            comp.setForeground(table_on_proses.getForeground());
                        }
                    } else if ((int) table_on_proses.getValueAt(row, 5) <= 6) {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(Color.green);
                            comp.setForeground(table_on_proses.getForeground());
                        }
                    } else {
                        if (isSelected) {
                            comp.setBackground(table_on_proses.getSelectionBackground());
                            comp.setForeground(table_on_proses.getSelectionForeground());
                        } else {
                            comp.setBackground(table_on_proses.getBackground());
                            comp.setForeground(table_on_proses.getForeground());
                        }
                    }
                    return comp;
                }
            });
            table_on_proses.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex);
            Logger.getLogger(JPanel_DataBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
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

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel_stok_kartu = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        table_stok_per_kartu = new javax.swing.JTable();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_Data_LP = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        tb_LP = new javax.swing.JTable();
        jLabel31 = new javax.swing.JLabel();
        label_berat_lp = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        label_total_lp = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        label_keping_lp = new javax.swing.JLabel();
        Date_LP2 = new com.toedter.calendar.JDateChooser();
        Date_LP1 = new com.toedter.calendar.JDateChooser();
        button_filter_LP = new javax.swing.JButton();
        jPanel_Data_Keluar = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        label_berat_keluar = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        label_total_keluar = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        tb_keluar = new javax.swing.JTable();
        jLabel36 = new javax.swing.JLabel();
        label_keping_keluar = new javax.swing.JLabel();
        jPanel_Data_KartuCMP = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        label_berat_cmp = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        label_total_cmp = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tb_cmp = new javax.swing.JTable();
        jLabel38 = new javax.swing.JLabel();
        label_keping_cmp = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Table_stock_bahan_baku = new javax.swing.JTable();
        ComboBox_Search_grade = new javax.swing.JComboBox<>();
        CheckBox_show_All_Cards = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        label_no_kartu = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        label_total_sisa_kpg = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        label_total_sisa_gram = new javax.swing.JLabel();
        CheckBox_show_dataMasihSisa = new javax.swing.JCheckBox();
        button_search_stock = new javax.swing.JButton();
        button_search_data_bahan_baku = new javax.swing.JButton();
        txt_search_data_bahan_baku = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        Date_FilterStok = new com.toedter.calendar.JDateChooser();
        button_simpan_stok_terakhir = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        label_total_stok_kpg = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        label_total_stok_gram = new javax.swing.JLabel();
        jPanel_stok_grade = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        button_search = new javax.swing.JButton();
        button_export = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_stok_per_grade = new javax.swing.JTable();
        Date_filter1 = new com.toedter.calendar.JDateChooser();
        ComboBox_FilterBentuk = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txt_berat_LP = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txt_kpg_LP = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txt_kpg_masuk = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txt_berat_masuk = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txt_kpg_sisa = new javax.swing.JLabel();
        txt_berat_sisa = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txt_kpg_jual = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txt_berat_jual = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_KartuMasuk = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        label_grade = new javax.swing.JLabel();
        ComboBox_FilterBulu = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        ComboBox_FilterWarna = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        button_print = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        ComboBox_FilterKategori = new javax.swing.JComboBox<>();
        txt_stok_sub = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txt_stok_waleta = new javax.swing.JLabel();
        txt_stok_unworkable = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txt_stok_bp = new javax.swing.JLabel();
        jPanel_ketahanan_stok = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        button_refresh_ketahananBaku = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_stok_for_waleta = new javax.swing.JTable();
        Date_filter2 = new com.toedter.calendar.JDateChooser();
        txt_stok_bp1 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        txt_stok_unworkable1 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        txt_stok_sub1 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        table_stok_for_sub = new javax.swing.JTable();
        jLabel57 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        table_stok_for_bp = new javax.swing.JTable();
        jLabel58 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        table_stok_for_jual = new javax.swing.JTable();
        jLabel60 = new javax.swing.JLabel();
        txt_pengeluaran_sub = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        txt_ketahanan_sub = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        txt_tanggal_end_sub = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        table_ketahanan_stok_waleta = new javax.swing.JTable();
        txt_tanggal_end_waleta = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        table_on_proses = new javax.swing.JTable();
        jLabel59 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        txt_total_proses_grading = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        jPanel_stok_kartu.setBackground(new java.awt.Color(255, 255, 255));

        table_stok_per_kartu.setAutoCreateRowSorter(true);
        table_stok_per_kartu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_stok_per_kartu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu Waleta", "Nama Supplier", "Rumah Burung", "Tgl Masuk", "Tgl Panen", "Tgl Grading", "Tgl Timbang", "Nitrit", "Kadar Air (%)", "Berat Awal", "Kpg Real", "Berat Real", "Stok Kpg", "Stok Gram", "Last Stok"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_stok_per_kartu.getTableHeader().setReorderingAllowed(false);
        jScrollPane9.setViewportView(table_stok_per_kartu);

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jPanel_Data_LP.setBackground(new java.awt.Color(255, 255, 255));

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel24.setText("DATA LAPORAN PRODUKSI");

        tb_LP.setAutoCreateRowSorter(true);
        tb_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tb_LP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No LP", "Grade LP", "Tgl LP", "Ruang", "Kpg", "Berat"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
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
        tb_LP.setRowSelectionAllowed(false);
        tb_LP.getTableHeader().setReorderingAllowed(false);
        jScrollPane10.setViewportView(tb_LP);

        jLabel31.setBackground(new java.awt.Color(255, 255, 255));
        jLabel31.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel31.setText("Total Berat :");

        label_berat_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_lp.setText("0");

        jLabel27.setBackground(new java.awt.Color(255, 255, 255));
        jLabel27.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel27.setText("Total Data :");

        label_total_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_lp.setText("0");

        jLabel32.setBackground(new java.awt.Color(255, 255, 255));
        jLabel32.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel32.setText("Total Keping :");

        label_keping_lp.setBackground(new java.awt.Color(255, 255, 255));
        label_keping_lp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping_lp.setText("0");

        Date_LP2.setBackground(new java.awt.Color(255, 255, 255));
        Date_LP2.setDateFormatString("dd/MM/yyyy");
        Date_LP2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        Date_LP1.setBackground(new java.awt.Color(255, 255, 255));
        Date_LP1.setDateFormatString("dd/MM/yyyy");
        Date_LP1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_filter_LP.setBackground(new java.awt.Color(255, 255, 255));
        button_filter_LP.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_filter_LP.setText("Search");
        button_filter_LP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_filter_LPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel_Data_LPLayout = new javax.swing.GroupLayout(jPanel_Data_LP);
        jPanel_Data_LP.setLayout(jPanel_Data_LPLayout);
        jPanel_Data_LPLayout.setHorizontalGroup(
            jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_LPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_LPLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Date_LP1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_LP2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_filter_LP))
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_LPLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel32)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_keping_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_berat_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_lp, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel_Data_LPLayout.setVerticalGroup(
            jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_LPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(button_filter_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(Date_LP2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Date_LP1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Data_LPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_lp)
                    .addComponent(jLabel27)
                    .addComponent(label_berat_lp)
                    .addComponent(jLabel31)
                    .addComponent(label_keping_lp)
                    .addComponent(jLabel32))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Laporan Produksi", jPanel_Data_LP);

        jPanel_Data_Keluar.setBackground(new java.awt.Color(255, 255, 255));

        jLabel25.setBackground(new java.awt.Color(255, 255, 255));
        jLabel25.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel25.setText("DATA BAHAN BAKU KELUAR");

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel33.setText("Total Berat :");

        label_berat_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_keluar.setText("0");

        jLabel29.setBackground(new java.awt.Color(255, 255, 255));
        jLabel29.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel29.setText("Total Data :");

        label_total_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_total_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_keluar.setText("0");

        tb_keluar.setAutoCreateRowSorter(true);
        tb_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tb_keluar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Keluar", "No Kartu", "Grade", "Tanggal Keluar", "Customer", "Jumlah Keping", "Total Berat"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
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
        tb_keluar.setRowSelectionAllowed(false);
        tb_keluar.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(tb_keluar);

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel36.setText("Total Keping :");

        label_keping_keluar.setBackground(new java.awt.Color(255, 255, 255));
        label_keping_keluar.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping_keluar.setText("0");

        javax.swing.GroupLayout jPanel_Data_KeluarLayout = new javax.swing.GroupLayout(jPanel_Data_Keluar);
        jPanel_Data_Keluar.setLayout(jPanel_Data_KeluarLayout);
        jPanel_Data_KeluarLayout.setHorizontalGroup(
            jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_KeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_KeluarLayout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_KeluarLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_keping_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_berat_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel_Data_KeluarLayout.setVerticalGroup(
            jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_KeluarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_berat_keluar)
                        .addComponent(jLabel33)
                        .addComponent(label_keping_keluar)
                        .addComponent(jLabel36))
                    .addGroup(jPanel_Data_KeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_keluar)
                        .addComponent(jLabel29)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Jual Bahan Baku", jPanel_Data_Keluar);

        jPanel_Data_KartuCMP.setBackground(new java.awt.Color(255, 255, 255));

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Trebuchet MS", 1, 14)); // NOI18N
        jLabel28.setText("DATA KARTU CAMPURAN");

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel37.setText("Total Berat :");

        label_berat_cmp.setBackground(new java.awt.Color(255, 255, 255));
        label_berat_cmp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_berat_cmp.setText("0");

        jLabel30.setBackground(new java.awt.Color(255, 255, 255));
        jLabel30.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel30.setText("Total Data :");

        label_total_cmp.setBackground(new java.awt.Color(255, 255, 255));
        label_total_cmp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_cmp.setText("0");

        tb_cmp.setAutoCreateRowSorter(true);
        tb_cmp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tb_cmp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Kartu", "Grade", "Tanggal Keluar", "Jumlah Keping", "Total Berat"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tb_cmp.setRowSelectionAllowed(false);
        tb_cmp.getTableHeader().setReorderingAllowed(false);
        jScrollPane12.setViewportView(tb_cmp);

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel38.setText("Total Keping :");

        label_keping_cmp.setBackground(new java.awt.Color(255, 255, 255));
        label_keping_cmp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_keping_cmp.setText("0");

        javax.swing.GroupLayout jPanel_Data_KartuCMPLayout = new javax.swing.GroupLayout(jPanel_Data_KartuCMP);
        jPanel_Data_KartuCMP.setLayout(jPanel_Data_KartuCMPLayout);
        jPanel_Data_KartuCMPLayout.setHorizontalGroup(
            jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_KartuCMPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_KartuCMPLayout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_Data_KartuCMPLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_keping_cmp, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_berat_cmp, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_cmp, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel_Data_KartuCMPLayout.setVerticalGroup(
            jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_Data_KartuCMPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_berat_cmp)
                        .addComponent(jLabel37)
                        .addComponent(label_keping_cmp)
                        .addComponent(jLabel38))
                    .addGroup(jPanel_Data_KartuCMPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(label_total_cmp)
                        .addComponent(jLabel30)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Data Kartu Campuran", jPanel_Data_KartuCMP);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Data Grading Bahan Baku", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Grade Bahan Baku :");

        Table_stock_bahan_baku.setAutoCreateRowSorter(true);
        Table_stock_bahan_baku.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Kode Grade", "Tot Kpg", "Tot Gram", "Sisa Kpg", "Sisa Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
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
        Table_stock_bahan_baku.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(Table_stock_bahan_baku);

        ComboBox_Search_grade.setEditable(true);
        ComboBox_Search_grade.setFont(new java.awt.Font("Yu Gothic UI", 0, 11)); // NOI18N
        ComboBox_Search_grade.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        CheckBox_show_All_Cards.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_show_All_Cards.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_show_All_Cards.setText("Show All");
        CheckBox_show_All_Cards.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_show_All_CardsItemStateChanged(evt);
            }
        });

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("No. Kartu :");

        label_no_kartu.setBackground(new java.awt.Color(255, 255, 255));
        label_no_kartu.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_no_kartu.setText("KARTU");

        jLabel34.setBackground(new java.awt.Color(255, 255, 255));
        jLabel34.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel34.setText("Total Sisa Keping :");

        label_total_sisa_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sisa_kpg.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_sisa_kpg.setText("0");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel35.setText("Total Sisa Berat :");

        label_total_sisa_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_sisa_gram.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        label_total_sisa_gram.setText("0");

        CheckBox_show_dataMasihSisa.setBackground(new java.awt.Color(255, 255, 255));
        CheckBox_show_dataMasihSisa.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        CheckBox_show_dataMasihSisa.setText("Lihat Data yang masih ada sisa");
        CheckBox_show_dataMasihSisa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBox_show_dataMasihSisaItemStateChanged(evt);
            }
        });

        button_search_stock.setBackground(new java.awt.Color(255, 255, 255));
        button_search_stock.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_stock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_stockActionPerformed(evt);
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
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search_stock, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_no_kartu)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CheckBox_show_All_Cards))
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(CheckBox_show_dataMasihSisa)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_sisa_kpg)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_total_sisa_gram)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_Search_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_no_kartu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(button_search_stock, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckBox_show_All_Cards, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_total_sisa_gram)
                    .addComponent(jLabel35)
                    .addComponent(label_total_sisa_kpg)
                    .addComponent(jLabel34)
                    .addComponent(CheckBox_show_dataMasihSisa, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        button_search_data_bahan_baku.setBackground(new java.awt.Color(255, 255, 255));
        button_search_data_bahan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search_data_bahan_baku.setText("Search");
        button_search_data_bahan_baku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_search_data_bahan_bakuActionPerformed(evt);
            }
        });

        txt_search_data_bahan_baku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txt_search_data_bahan_baku.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_search_data_bahan_bakuKeyPressed(evt);
            }
        });

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel23.setText("No Kartu Waleta :");

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel26.setText("Data per Tanggal :");

        Date_FilterStok.setBackground(new java.awt.Color(255, 255, 255));
        Date_FilterStok.setDateFormatString("dd MMMM yyyy");
        Date_FilterStok.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        button_simpan_stok_terakhir.setBackground(new java.awt.Color(255, 255, 255));
        button_simpan_stok_terakhir.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_simpan_stok_terakhir.setText("Save Last Stok");
        button_simpan_stok_terakhir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_simpan_stok_terakhirActionPerformed(evt);
            }
        });

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel39.setText("Total Stok Kpg :");

        label_total_stok_kpg.setBackground(new java.awt.Color(255, 255, 255));
        label_total_stok_kpg.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_stok_kpg.setText("0");

        jLabel41.setBackground(new java.awt.Color(255, 255, 255));
        jLabel41.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel41.setText("Total Stok Gram :");

        label_total_stok_gram.setBackground(new java.awt.Color(255, 255, 255));
        label_total_stok_gram.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_total_stok_gram.setText("0");

        javax.swing.GroupLayout jPanel_stok_kartuLayout = new javax.swing.GroupLayout(jPanel_stok_kartu);
        jPanel_stok_kartu.setLayout(jPanel_stok_kartuLayout);
        jPanel_stok_kartuLayout.setHorizontalGroup(
            jPanel_stok_kartuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_stok_kartuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_stok_kartuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
                    .addGroup(jPanel_stok_kartuLayout.createSequentialGroup()
                        .addGroup(jPanel_stok_kartuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_stok_kartuLayout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_search_data_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_FilterStok, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_search_data_bahan_baku)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_simpan_stok_terakhir))
                            .addGroup(jPanel_stok_kartuLayout.createSequentialGroup()
                                .addComponent(jLabel39)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_stok_kpg)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel41)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label_total_stok_gram)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_stok_kartuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel_stok_kartuLayout.setVerticalGroup(
            jPanel_stok_kartuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_stok_kartuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_stok_kartuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_stok_kartuLayout.createSequentialGroup()
                        .addGroup(jPanel_stok_kartuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_stok_kartuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_search_data_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_stok_kartuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(button_search_data_bahan_baku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_simpan_stok_terakhir, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Date_FilterStok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_stok_kartuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_stok_kpg, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_total_stok_gram, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane9))
                    .addGroup(jPanel_stok_kartuLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jTabbedPane2.addTab("DATA STOK BAKU PER KARTU", jPanel_stok_kartu);

        jPanel_stok_grade.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Data Pada Tanggal :");

        button_search.setBackground(new java.awt.Color(255, 255, 255));
        button_search.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_search.setText("Search");
        button_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_searchActionPerformed(evt);
            }
        });

        button_export.setBackground(new java.awt.Color(255, 255, 255));
        button_export.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_export.setText("Export To Excel");
        button_export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exportActionPerformed(evt);
            }
        });

        table_stok_per_grade.setAutoCreateRowSorter(true);
        table_stok_per_grade.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        table_stok_per_grade.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Grade", "Kpg Awal", "Gram Awal", "Kpg LP", "Gram LP", "Kpg Jual", "Gram Jual", "Kpg CMP", "Gram CMP", "Sisa Kpg", "Sisa Gram", "Kategori Proses", "jenis_bulu"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_stok_per_grade.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(table_stok_per_grade);
        if (table_stok_per_grade.getColumnModel().getColumnCount() > 0) {
            table_stok_per_grade.getColumnModel().getColumn(0).setHeaderValue("Jenis Bentuk");
        }

        Date_filter1.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter1.setDate(today);
        Date_filter1.setDateFormatString("dd MMMM yyyy");
        Date_filter1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        ComboBox_FilterBentuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_FilterBentuk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Mangkok", "Segitiga", "Oval", "Pch / Lbg", "-" }));

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Jenis Bentuk :");

        txt_berat_LP.setBackground(new java.awt.Color(255, 255, 255));
        txt_berat_LP.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_berat_LP.setText("0");

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Berat LP :");

        txt_kpg_LP.setBackground(new java.awt.Color(255, 255, 255));
        txt_kpg_LP.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_kpg_LP.setText("0");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Keping LP :");

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Keping Masuk :");

        txt_kpg_masuk.setBackground(new java.awt.Color(255, 255, 255));
        txt_kpg_masuk.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_kpg_masuk.setText("0");

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText("Berat Masuk :");

        txt_berat_masuk.setBackground(new java.awt.Color(255, 255, 255));
        txt_berat_masuk.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_berat_masuk.setText("0");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Keping Sisa :");

        txt_kpg_sisa.setBackground(new java.awt.Color(255, 255, 255));
        txt_kpg_sisa.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_kpg_sisa.setText("0");

        txt_berat_sisa.setBackground(new java.awt.Color(255, 255, 255));
        txt_berat_sisa.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_berat_sisa.setText("0");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Berat Sisa :");

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setText("Keping Jual :");

        txt_kpg_jual.setBackground(new java.awt.Color(255, 255, 255));
        txt_kpg_jual.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_kpg_jual.setText("0");

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Berat Jual :");

        txt_berat_jual.setBackground(new java.awt.Color(255, 255, 255));
        txt_berat_jual.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_berat_jual.setText("0");

        tabel_KartuMasuk.setAutoCreateRowSorter(true);
        tabel_KartuMasuk.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tabel_KartuMasuk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No Kartu", "Tgl Masuk", "Kpg Real", "Berat Real"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabel_KartuMasuk.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tabel_KartuMasuk);

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel12.setText("No Kartu Waleta untuk grade :");

        label_grade.setBackground(new java.awt.Color(255, 255, 255));
        label_grade.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        label_grade.setText("-");

        ComboBox_FilterBulu.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_FilterBulu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "BRS/BR", "BR", "BS", "BB", "BB2", "-" }));

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel13.setText("Jenis Bulu :");

        ComboBox_FilterWarna.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_FilterWarna.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Warna 1", "Warna 2", "Warna 3", "Garis Ringan", "Garis Berat", "Kaki Kuning", "Kuning", "Orange", "Hijau Berat", "Hijau Ringan", "Kaki Merah", "-" }));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel15.setText("Jenis Warna :");

        button_print.setBackground(new java.awt.Color(255, 255, 255));
        button_print.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_print.setText("Print");
        button_print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_printActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel16.setText("Kategori Proses:");

        ComboBox_FilterKategori.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        ComboBox_FilterKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        txt_stok_sub.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_sub.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_stok_sub.setText("0");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("STOK SUB :");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("STOK WALETA :");

        txt_stok_waleta.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_waleta.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_stok_waleta.setText("0");

        txt_stok_unworkable.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_unworkable.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_stok_unworkable.setText("0");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("UNWORKABLE :");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("STOK BP :");

        txt_stok_bp.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_bp.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_stok_bp.setText("0");

        javax.swing.GroupLayout jPanel_stok_gradeLayout = new javax.swing.GroupLayout(jPanel_stok_grade);
        jPanel_stok_grade.setLayout(jPanel_stok_gradeLayout);
        jPanel_stok_gradeLayout.setHorizontalGroup(
            jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_stok_gradeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_stok_gradeLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_FilterBentuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_FilterBulu, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_FilterWarna, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_FilterKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_search)
                        .addGap(0, 95, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_stok_gradeLayout.createSequentialGroup()
                        .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_stok_gradeLayout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_kpg_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_berat_LP, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_stok_gradeLayout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_kpg_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_berat_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_stok_gradeLayout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_kpg_sisa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_berat_sisa, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_stok_gradeLayout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_kpg_jual, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_berat_jual, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_stok_gradeLayout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_stok_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_stok_gradeLayout.createSequentialGroup()
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_stok_waleta, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_stok_gradeLayout.createSequentialGroup()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_stok_unworkable, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_stok_gradeLayout.createSequentialGroup()
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_stok_bp, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(button_print)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_export))
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel_stok_gradeLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_grade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel_stok_gradeLayout.setVerticalGroup(
            jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_stok_gradeLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(label_grade, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Date_filter1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ComboBox_FilterKategori, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button_search, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ComboBox_FilterWarna, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ComboBox_FilterBulu, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ComboBox_FilterBentuk, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                    .addGroup(jPanel_stok_gradeLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_stok_gradeLayout.createSequentialGroup()
                        .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(button_export, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(button_print, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_kpg_masuk, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_berat_masuk, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_berat_LP)
                            .addComponent(jLabel5)
                            .addComponent(txt_kpg_LP)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_berat_jual)
                            .addComponent(jLabel10)
                            .addComponent(txt_kpg_jual)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_berat_sisa)
                            .addComponent(jLabel14)
                            .addComponent(txt_kpg_sisa)
                            .addComponent(jLabel11)))
                    .addGroup(jPanel_stok_gradeLayout.createSequentialGroup()
                        .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_stok_waleta, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_stok_sub)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_stok_bp)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_stok_gradeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_stok_unworkable)
                            .addComponent(jLabel19))))
                .addContainerGap())
        );

        jTabbedPane2.addTab("DATA STOK BAKU PER GRADE", jPanel_stok_grade);

        jPanel_ketahanan_stok.setBackground(new java.awt.Color(255, 255, 255));

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel21.setText("Data Pada Tanggal :");

        button_refresh_ketahananBaku.setBackground(new java.awt.Color(255, 255, 255));
        button_refresh_ketahananBaku.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        button_refresh_ketahananBaku.setText("Search");
        button_refresh_ketahananBaku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_refresh_ketahananBakuActionPerformed(evt);
            }
        });

        table_stok_for_waleta.setAutoCreateRowSorter(true);
        table_stok_for_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_stok_for_waleta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Jenis Bentuk", "Jenis Bulu", "Stok Keping", "Stok Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_stok_for_waleta.setRowHeight(20);
        table_stok_for_waleta.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(table_stok_for_waleta);
        if (table_stok_for_waleta.getColumnModel().getColumnCount() > 0) {
            table_stok_for_waleta.getColumnModel().getColumn(0).setHeaderValue("Jenis Bentuk");
        }

        Date_filter2.setBackground(new java.awt.Color(255, 255, 255));
        Date_filter2.setDate(today);
        Date_filter2.setDateFormatString("dd MMMM yyyy");
        Date_filter2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txt_stok_bp1.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_bp1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_stok_bp1.setText("0");

        jLabel51.setBackground(new java.awt.Color(255, 255, 255));
        jLabel51.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel51.setText("STOK BP :");

        txt_stok_unworkable1.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_unworkable1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_stok_unworkable1.setText("0");

        jLabel53.setBackground(new java.awt.Color(255, 255, 255));
        jLabel53.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel53.setText("UNWORKABLE :");

        jLabel54.setBackground(new java.awt.Color(255, 255, 255));
        jLabel54.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel54.setText("STOK SUB :");

        txt_stok_sub1.setBackground(new java.awt.Color(255, 255, 255));
        txt_stok_sub1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_stok_sub1.setText("0");

        jLabel55.setBackground(new java.awt.Color(255, 255, 255));
        jLabel55.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel55.setText("WORKABLE MATERIAL FOR WALETA");

        jLabel56.setBackground(new java.awt.Color(255, 255, 255));
        jLabel56.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel56.setText("WORKABLE MATERIAL FOR SUB");

        table_stok_for_sub.setAutoCreateRowSorter(true);
        table_stok_for_sub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_stok_for_sub.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Jenis Bentuk", "Jenis Bulu", "Stok Keping", "Stok Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_stok_for_sub.setRowHeight(20);
        table_stok_for_sub.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(table_stok_for_sub);
        if (table_stok_for_sub.getColumnModel().getColumnCount() > 0) {
            table_stok_for_sub.getColumnModel().getColumn(0).setHeaderValue("Jenis Bentuk");
        }

        jLabel57.setBackground(new java.awt.Color(255, 255, 255));
        jLabel57.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel57.setText("WORKABLE BY PRODUCT");

        table_stok_for_bp.setAutoCreateRowSorter(true);
        table_stok_for_bp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_stok_for_bp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Stok Keping", "Stok Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        table_stok_for_bp.setRowHeight(20);
        table_stok_for_bp.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(table_stok_for_bp);
        if (table_stok_for_bp.getColumnModel().getColumnCount() > 0) {
            table_stok_for_bp.getColumnModel().getColumn(0).setHeaderValue("Jenis Bentuk");
        }

        jLabel58.setBackground(new java.awt.Color(255, 255, 255));
        jLabel58.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel58.setText("UNWORKABLE MATERIAL");

        table_stok_for_jual.setAutoCreateRowSorter(true);
        table_stok_for_jual.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_stok_for_jual.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Grade", "Stok Keping", "Stok Gram"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class
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
        table_stok_for_jual.setRowHeight(20);
        table_stok_for_jual.getTableHeader().setReorderingAllowed(false);
        jScrollPane7.setViewportView(table_stok_for_jual);
        if (table_stok_for_jual.getColumnModel().getColumnCount() > 0) {
            table_stok_for_jual.getColumnModel().getColumn(0).setHeaderValue("Jenis Bentuk");
        }

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel60.setText("Rata2 Pengeluaran Baku untuk SUB :");

        txt_pengeluaran_sub.setBackground(new java.awt.Color(255, 255, 255));
        txt_pengeluaran_sub.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_pengeluaran_sub.setText("0");

        jLabel61.setBackground(new java.awt.Color(255, 255, 255));
        jLabel61.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel61.setText("Ketahanan Sub (Hari) :");

        txt_ketahanan_sub.setBackground(new java.awt.Color(255, 255, 255));
        txt_ketahanan_sub.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_ketahanan_sub.setText("0");

        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel62.setText("Tanggal perkiraan stok habis :");

        txt_tanggal_end_sub.setBackground(new java.awt.Color(255, 255, 255));
        txt_tanggal_end_sub.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_tanggal_end_sub.setText("0");

        table_ketahanan_stok_waleta.setAutoCreateRowSorter(true);
        table_ketahanan_stok_waleta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_ketahanan_stok_waleta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Jenis Bulu", "Stok (Gram)", "Anak CBT", "Kpg/Anak", "Gr/Kpg", "Max Load/hari", "Ketahanan (Hari)"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class
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
        table_ketahanan_stok_waleta.setRowHeight(20);
        table_ketahanan_stok_waleta.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(table_ketahanan_stok_waleta);
        if (table_ketahanan_stok_waleta.getColumnModel().getColumnCount() > 0) {
            table_ketahanan_stok_waleta.getColumnModel().getColumn(3).setHeaderValue("Kpg/Anak");
            table_ketahanan_stok_waleta.getColumnModel().getColumn(4).setHeaderValue("Gr/Kpg");
            table_ketahanan_stok_waleta.getColumnModel().getColumn(5).setHeaderValue("Max Load/hari");
            table_ketahanan_stok_waleta.getColumnModel().getColumn(6).setHeaderValue("Ketahanan (Hari)");
        }

        txt_tanggal_end_waleta.setBackground(new java.awt.Color(255, 255, 255));
        txt_tanggal_end_waleta.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_tanggal_end_waleta.setText("0");

        jLabel63.setBackground(new java.awt.Color(255, 255, 255));
        jLabel63.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel63.setText("Tanggal perkiraan stok habis :");

        table_on_proses.setAutoCreateRowSorter(true);
        table_on_proses.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        table_on_proses.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No", "No Kartu", "Tgl Masuk", "Supplier", "Berat (Gr)", "Hari"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
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
        table_on_proses.setRowHeight(20);
        table_on_proses.getTableHeader().setReorderingAllowed(false);
        jScrollPane13.setViewportView(table_on_proses);
        if (table_on_proses.getColumnModel().getColumnCount() > 0) {
            table_on_proses.getColumnModel().getColumn(0).setMinWidth(50);
            table_on_proses.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        jLabel59.setBackground(new java.awt.Color(255, 255, 255));
        jLabel59.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel59.setText("BAKU PROSES GRADING");

        jLabel64.setBackground(new java.awt.Color(255, 255, 255));
        jLabel64.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel64.setText("Total Berat :");

        txt_total_proses_grading.setBackground(new java.awt.Color(255, 255, 255));
        txt_total_proses_grading.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        txt_total_proses_grading.setText("0");

        javax.swing.GroupLayout jPanel_ketahanan_stokLayout = new javax.swing.GroupLayout(jPanel_ketahanan_stok);
        jPanel_ketahanan_stok.setLayout(jPanel_ketahanan_stokLayout);
        jPanel_ketahanan_stokLayout.setHorizontalGroup(
            jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                        .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel55)
                            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_refresh_ketahananBaku))
                            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                                .addComponent(jLabel63)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_tanggal_end_waleta)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                    .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                        .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                                .addComponent(jLabel64)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_total_proses_grading, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel56)
                            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                                .addComponent(jLabel54)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_stok_sub1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                                .addComponent(jLabel60)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_pengeluaran_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                                .addComponent(jLabel61)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_ketahanan_sub, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                                .addComponent(jLabel62)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_tanggal_end_sub))
                            .addComponent(jLabel59))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                        .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_stok_unworkable1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel57)
                        .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                            .addComponent(jLabel51)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_stok_bp1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel58)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_ketahanan_stokLayout.setVerticalGroup(
            jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_ketahanan_stokLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                        .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Date_filter2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(button_refresh_ketahananBaku, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel57, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                            .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                        .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
                            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txt_stok_bp1)
                                    .addComponent(jLabel51))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_stok_sub1)
                            .addComponent(jLabel54)
                            .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel_ketahanan_stokLayout.createSequentialGroup()
                                .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel60)
                                    .addComponent(txt_pengeluaran_sub))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel61)
                                    .addComponent(txt_ketahanan_sub))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel62)
                                    .addComponent(txt_tanggal_end_sub))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_total_proses_grading)
                        .addComponent(jLabel64))
                    .addGroup(jPanel_ketahanan_stokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_stok_unworkable1)
                        .addComponent(jLabel53)
                        .addComponent(jLabel63)
                        .addComponent(txt_tanggal_end_waleta)))
                .addContainerGap())
        );

        jTabbedPane2.addTab("KETAHANAN STOK BAKU", jPanel_ketahanan_stok);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txt_search_data_bahan_bakuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_search_data_bahan_bakuKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            refreshTable_BahanBaku();
            DefaultTableModel model_lp = (DefaultTableModel) tb_LP.getModel();
            model_lp.setRowCount(0);
            DefaultTableModel model_keluar = (DefaultTableModel) tb_keluar.getModel();
            model_keluar.setRowCount(0);
        }
    }//GEN-LAST:event_txt_search_data_bahan_bakuKeyPressed

    private void button_search_data_bahan_bakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_data_bahan_bakuActionPerformed
        // TODO add your handling code here:
        refreshTable_BahanBaku();
        DefaultTableModel model_lp = (DefaultTableModel) tb_LP.getModel();
        model_lp.setRowCount(0);
        DefaultTableModel model_keluar = (DefaultTableModel) tb_keluar.getModel();
        model_keluar.setRowCount(0);
    }//GEN-LAST:event_button_search_data_bahan_bakuActionPerformed

    private void CheckBox_show_All_CardsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_show_All_CardsItemStateChanged
        // TODO add your handling code here:
        refreshTable_Stock_grade();
    }//GEN-LAST:event_CheckBox_show_All_CardsItemStateChanged

    private void button_filter_LPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_filter_LPActionPerformed
        // TODO add your handling code here:
        refreshTable_LP();
    }//GEN-LAST:event_button_filter_LPActionPerformed

    private void CheckBox_show_dataMasihSisaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBox_show_dataMasihSisaItemStateChanged
        // TODO add your handling code here:
        refreshTable_Stock_grade();
    }//GEN-LAST:event_CheckBox_show_dataMasihSisaItemStateChanged

    private void button_search_stockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_search_stockActionPerformed
        // TODO add your handling code here:
        refreshTable_Stock_grade();
    }//GEN-LAST:event_button_search_stockActionPerformed

    private void button_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_searchActionPerformed
        refresh_STOK_PER_GRADE();
    }//GEN-LAST:event_button_searchActionPerformed

    private void button_exportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exportActionPerformed
        DefaultTableModel model = (DefaultTableModel) table_stok_per_grade.getModel();
        ExportToExcel.writeToExcel(model, jPanel1);
    }//GEN-LAST:event_button_exportActionPerformed

    private void button_printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_printActionPerformed
        try {
            // TODO add your handling code here:
            //            DefaultTableModel Table = (DefaultTableModel)Table_laporan_produksi.getModel();

            JasperDesign JASP_DESIGN = JRXmlLoader.load("Report\\Stok_Bahan_Baku.jrxml");
            JasperReport JASP_REP = JasperCompileManager.compileReport(JASP_DESIGN);
            Map<String, Object> params = new HashMap<>();
            SimpleDateFormat dateFormatPrint = new SimpleDateFormat("dd MMMMM yyyy");
            params.put("tanggalPrint", "TANGGAL " + dateFormatPrint.format(Date_filter1.getDate()).toUpperCase());
            params.put("1BRSBRW1KPG", val1BRSBRW1KPG);
            params.put("1BRSBRW1GR", val1BRSBRW1GR);
            params.put("1BRSBRW2KPG", val1BRSBRW2KPG);
            params.put("1BRSBRW2GR", val1BRSBRW2GR);
            params.put("1BRSBRW3KPG", val1BRSBRW3KPG);
            params.put("1BRSBRW3GR", val1BRSBRW3GR);
            params.put("1BRSBR-KPG", val1BRSBR_KPG);
            params.put("1BRSBR-GR", val1BRSBR_GR);
            params.put("1BRW1KPG", val1BRW1KPG);
            params.put("1BRW1GR", val1BRW1GR);
            params.put("1BRW2KPG", val1BRW2KPG);
            params.put("1BRW2GR", val1BRW2GR);
            params.put("1BRW3KPG", val1BRW3KPG);
            params.put("1BRW3GR", val1BRW3GR);
            params.put("1BR-KPG", val1BR_KPG);
            params.put("1BR-GR", val1BR_GR);
            params.put("1BSW1KPG", val1BSW1KPG);
            params.put("1BSW1GR", val1BSW1GR);
            params.put("1BSW2KPG", val1BSW2KPG);
            params.put("1BSW2GR", val1BSW2GR);
            params.put("1BSW3KPG", val1BSW3KPG);
            params.put("1BSW3GR", val1BSW3GR);
            params.put("1BS-KPG", val1BS_KPG);
            params.put("1BS-GR", val1BS_GR);
            params.put("1BBW1KPG", val1BBW1KPG);
            params.put("1BBW1GR", val1BBW1GR);
            params.put("1BBW2KPG", val1BBW2KPG);
            params.put("1BBW2GR", val1BBW2GR);
            params.put("1BBW3KPG", val1BBW3KPG);
            params.put("1BBW3GR", val1BBW3GR);
            params.put("1BB-KPG", val1BB_KPG);
            params.put("1BB-GR", val1BB_GR);
            params.put("1BB2W1KPG", val1BB2W1KPG);
            params.put("1BB2W1GR", val1BB2W1GR);
            params.put("1BB2W2KPG", val1BB2W2KPG);
            params.put("1BB2W2GR", val1BB2W2GR);
            params.put("1BB2W3KPG", val1BB2W3KPG);
            params.put("1BB2W3GR", val1BB2W3GR);
            params.put("1BB2-KPG", val1BB2_KPG);
            params.put("1BB2-GR", val1BB2_GR);
            params.put("1-W1KPG", new Long(0));
            params.put("1-W1GR", new Long(0));
            params.put("1-W2KPG", new Long(0));
            params.put("1-W2GR", new Long(0));
            params.put("1-W3KPG", new Long(0));
            params.put("1-W3GR", new Long(0));
            params.put("1--KPG", new Long(0));
            params.put("1--GR", new Long(0));
            params.put("2BRSBRW1KPG", val2BRSBRW1KPG);
            params.put("2BRSBRW1GR", val2BRSBRW1GR);
            params.put("2BRSBRW2KPG", val2BRSBRW2KPG);
            params.put("2BRSBRW2GR", val2BRSBRW2GR);
            params.put("2BRSBRW3KPG", val2BRSBRW3KPG);
            params.put("2BRSBRW3GR", val2BRSBRW3GR);
            params.put("2BRSBR-KPG", val2BRSBR_KPG);
            params.put("2BRSBR-GR", val2BRSBR_GR);
            params.put("2BRW1KPG", val2BRW1KPG);
            params.put("2BRW1GR", val2BRW1GR);
            params.put("2BRW2KPG", val2BRW2KPG);
            params.put("2BRW2GR", val2BRW2GR);
            params.put("2BRW3KPG", val2BRW3KPG);
            params.put("2BRW3GR", val2BRW3GR);
            params.put("2BR-KPG", val2BR_KPG);
            params.put("2BR-GR", val2BR_GR);
            params.put("2BSW1KPG", val2BSW1KPG);
            params.put("2BSW1GR", val2BSW1GR);
            params.put("2BSW2KPG", val2BSW2KPG);
            params.put("2BSW2GR", val2BSW2GR);
            params.put("2BSW3KPG", val2BSW3KPG);
            params.put("2BSW3GR", val2BSW3GR);
            params.put("2BS-KPG", val2BS_KPG);
            params.put("2BS-GR", val2BS_GR);
            params.put("2BBW1KPG", val2BBW1KPG);
            params.put("2BBW1GR", val2BBW1GR);
            params.put("2BBW2KPG", val2BBW2KPG);
            params.put("2BBW2GR", val2BBW2GR);
            params.put("2BBW3KPG", val2BBW3KPG);
            params.put("2BBW3GR", val2BBW3GR);
            params.put("2BB-KPG", val2BB_KPG);
            params.put("2BB-GR", val2BB_GR);
            params.put("2BB2W1KPG", val2BB2W1KPG);
            params.put("2BB2W1GR", val2BB2W1GR);
            params.put("2BB2W2KPG", val2BB2W2KPG);
            params.put("2BB2W2GR", val2BB2W2GR);
            params.put("2BB2W3KPG", val2BB2W3KPG);
            params.put("2BB2W3GR", val2BB2W3GR);
            params.put("2BB2-KPG", val2BB2_KPG);
            params.put("2BB2-GR", val2BB2_GR);
            params.put("2-W1KPG", new Long(0));
            params.put("2-W1GR", new Long(0));
            params.put("2-W2KPG", new Long(0));
            params.put("2-W2GR", new Long(0));
            params.put("2-W3KPG", new Long(0));
            params.put("2-W3GR", new Long(0));
            params.put("2--KPG", new Long(0));
            params.put("2--GR", new Long(0));
            params.put("3BRSBRW1KPG", val3BRSBRW1KPG);
            params.put("3BRSBRW1GR", val3BRSBRW1GR);
            params.put("3BRSBRW2KPG", val3BRSBRW2KPG);
            params.put("3BRSBRW2GR", val3BRSBRW2GR);
            params.put("3BRSBRW3KPG", val3BRSBRW3KPG);
            params.put("3BRSBRW3GR", val3BRSBRW3GR);
            params.put("3BRSBR-KPG", val3BRSBR_KPG);
            params.put("3BRSBR-GR", val3BRSBR_GR);
            params.put("3BRW1KPG", val3BRW1KPG);
            params.put("3BRW1GR", val3BRW1GR);
            params.put("3BRW2KPG", val3BRW2KPG);
            params.put("3BRW2GR", val3BRW2GR);
            params.put("3BRW3KPG", val3BRW3KPG);
            params.put("3BRW3GR", val3BRW3GR);
            params.put("3BR-KPG", val3BR_KPG);
            params.put("3BR-GR", val3BR_GR);
            params.put("3BSW1KPG", val3BSW1KPG);
            params.put("3BSW1GR", val3BSW1GR);
            params.put("3BSW2KPG", val3BSW2KPG);
            params.put("3BSW2GR", val3BSW2GR);
            params.put("3BSW3KPG", val3BSW3KPG);
            params.put("3BSW3GR", val3BSW3GR);
            params.put("3BS-KPG", val3BS_KPG);
            params.put("3BS-GR", val3BS_GR);
            params.put("3BBW1KPG", val3BBW1KPG);
            params.put("3BBW1GR", val3BBW1GR);
            params.put("3BBW2KPG", val3BBW2KPG);
            params.put("3BBW2GR", val3BBW2GR);
            params.put("3BBW3KPG", val3BBW3KPG);
            params.put("3BBW3GR", val3BBW3GR);
            params.put("3BB-KPG", val3BB_KPG);
            params.put("3BB-GR", val3BB_GR);
            params.put("3BB2W1KPG", val3BB2W1KPG);
            params.put("3BB2W1GR", val3BB2W1GR);
            params.put("3BB2W2KPG", val3BB2W2KPG);
            params.put("3BB2W2GR", val3BB2W2GR);
            params.put("3BB2W3KPG", val3BB2W3KPG);
            params.put("3BB2W3GR", val3BB2W3GR);
            params.put("3BB2-KPG", val3BB2_KPG);
            params.put("3BB2-GR", val3BB2_GR);
            params.put("3-W1KPG", new Long(0));
            params.put("3-W1GR", new Long(0));
            params.put("3-W2KPG", new Long(0));
            params.put("3-W2GR", new Long(0));
            params.put("3-W3KPG", new Long(0));
            params.put("3-W3GR", new Long(0));
            params.put("3--KPG", new Long(0));
            params.put("3--GR", new Long(0));
            params.put("MkLainBRSBRW1KPG", valMkLainBRSBRW1KPG);
            params.put("MkLainBRSBRW1GR", valMkLainBRSBRW1GR);
            params.put("MkLainBRSBRW2KPG", valMkLainBRSBRW2KPG);
            params.put("MkLainBRSBRW2GR", valMkLainBRSBRW2GR);
            params.put("MkLainBRSBRW3KPG", valMkLainBRSBRW3KPG);
            params.put("MkLainBRSBRW3GR", valMkLainBRSBRW3GR);
            params.put("MkLainBRSBR-KPG", valMkLainBRSBR_KPG);
            params.put("MkLainBRSBR-GR", valMkLainBRSBR_GR);
            params.put("MkLainBRW1KPG", valMkLainBRW1KPG);
            params.put("MkLainBRW1GR", valMkLainBRW1GR);
            params.put("MkLainBRW2KPG", valMkLainBRW2KPG);
            params.put("MkLainBRW2GR", valMkLainBRW2GR);
            params.put("MkLainBRW3KPG", valMkLainBRW3KPG);
            params.put("MkLainBRW3GR", valMkLainBRW3GR);
            params.put("MkLainBR-KPG", valMkLainBR_KPG);
            params.put("MkLainBR-GR", valMkLainBR_GR);
            params.put("MkLainBSW1KPG", valMkLainBSW1KPG);
            params.put("MkLainBSW1GR", valMkLainBSW1GR);
            params.put("MkLainBSW2KPG", valMkLainBSW2KPG);
            params.put("MkLainBSW2GR", valMkLainBSW2GR);
            params.put("MkLainBSW3KPG", valMkLainBSW3KPG);
            params.put("MkLainBSW3GR", valMkLainBSW3GR);
            params.put("MkLainBS-KPG", valMkLainBS_KPG);
            params.put("MkLainBS-GR", valMkLainBS_GR);
            params.put("MkLainBBW1KPG", valMkLainBBW1KPG);
            params.put("MkLainBBW1GR", valMkLainBBW1GR);
            params.put("MkLainBBW2KPG", valMkLainBBW2KPG);
            params.put("MkLainBBW2GR", valMkLainBBW2GR);
            params.put("MkLainBBW3KPG", valMkLainBBW3KPG);
            params.put("MkLainBBW3GR", valMkLainBBW3GR);
            params.put("MkLainBB-KPG", valMkLainBB_KPG);
            params.put("MkLainBB-GR", valMkLainBB_GR);
            params.put("MkLainBB2W1KPG", valMkLainBB2W1KPG);
            params.put("MkLainBB2W1GR", valMkLainBB2W1GR);
            params.put("MkLainBB2W2KPG", valMkLainBB2W2KPG);
            params.put("MkLainBB2W2GR", valMkLainBB2W2GR);
            params.put("MkLainBB2W3KPG", valMkLainBB2W3KPG);
            params.put("MkLainBB2W3GR", valMkLainBB2W3GR);
            params.put("MkLainBB2-KPG", valMkLainBB2_KPG);
            params.put("MkLainBB2-GR", valMkLainBB2_GR);
            params.put("MkLain-W1KPG", valMkLain_W1KPG + val1_W1KPG + val2_W1KPG + val3_W1KPG);
            params.put("MkLain-W1GR", valMkLain_W1GR + val1_W1GR + val2_W1GR + val3_W1GR);
            params.put("MkLain-W2KPG", valMkLain_W2KPG + val1_W2KPG + val2_W2KPG + val3_W2KPG);
            params.put("MkLain-W2GR", valMkLain_W2GR + val1_W2GR + val2_W2GR + val3_W2GR);
            params.put("MkLain-W3KPG", valMkLain_W3KPG + val1_W3KPG + val2_W3KPG + val3_W3KPG);
            params.put("MkLain-W3GR", valMkLain_W3GR + val1_W3GR + val2_W3GR + val3_W3GR);
            params.put("MkLain--KPG", valMkLain__KPG + val1__KPG + val2__KPG + val3__KPG);
            params.put("MkLain--GR", valMkLain__GR + val1__GR + val2__GR + val3__GR);
            params.put("SgtgBRSBRW1KPG", valSgtgBRSBRW1KPG);
            params.put("SgtgBRSBRW1GR", valSgtgBRSBRW1GR);
            params.put("SgtgBRSBRW2KPG", valSgtgBRSBRW2KPG);
            params.put("SgtgBRSBRW2GR", valSgtgBRSBRW2GR);
            params.put("SgtgBRSBRW3KPG", valSgtgBRSBRW3KPG);
            params.put("SgtgBRSBRW3GR", valSgtgBRSBRW3GR);
            params.put("SgtgBRSBR-KPG", valSgtgBRSBR_KPG);
            params.put("SgtgBRSBR-GR", valSgtgBRSBR_GR);
            params.put("SgtgBRW1KPG", valSgtgBRW1KPG);
            params.put("SgtgBRW1GR", valSgtgBRW1GR);
            params.put("SgtgBRW2KPG", valSgtgBRW2KPG);
            params.put("SgtgBRW2GR", valSgtgBRW2GR);
            params.put("SgtgBRW3KPG", valSgtgBRW3KPG);
            params.put("SgtgBRW3GR", valSgtgBRW3GR);
            params.put("SgtgBR-KPG", valSgtgBR_KPG);
            params.put("SgtgBR-GR", valSgtgBR_GR);
            params.put("SgtgBSW1KPG", valSgtgBSW1KPG);
            params.put("SgtgBSW1GR", valSgtgBSW1GR);
            params.put("SgtgBSW2KPG", valSgtgBSW2KPG);
            params.put("SgtgBSW2GR", valSgtgBSW2GR);
            params.put("SgtgBSW3KPG", valSgtgBSW3KPG);
            params.put("SgtgBSW3GR", valSgtgBSW3GR);
            params.put("SgtgBS-KPG", valSgtgBS_KPG);
            params.put("SgtgBS-GR", valSgtgBS_GR);
            params.put("SgtgBBW1KPG", valSgtgBBW1KPG);
            params.put("SgtgBBW1GR", valSgtgBBW1GR);
            params.put("SgtgBBW2KPG", valSgtgBBW2KPG);
            params.put("SgtgBBW2GR", valSgtgBBW2GR);
            params.put("SgtgBBW3KPG", valSgtgBBW3KPG);
            params.put("SgtgBBW3GR", valSgtgBBW3GR);
            params.put("SgtgBB-KPG", valSgtgBB_KPG);
            params.put("SgtgBB-GR", valSgtgBB_GR);
            params.put("SgtgBB2W1KPG", valSgtgBB2W1KPG);
            params.put("SgtgBB2W1GR", valSgtgBB2W1GR);
            params.put("SgtgBB2W2KPG", valSgtgBB2W2KPG);
            params.put("SgtgBB2W2GR", valSgtgBB2W2GR);
            params.put("SgtgBB2W3KPG", valSgtgBB2W3KPG);
            params.put("SgtgBB2W3GR", valSgtgBB2W3GR);
            params.put("SgtgBB2-KPG", valSgtgBB2_KPG);
            params.put("SgtgBB2-GR", valSgtgBB2_GR);
            params.put("Sgtg-W1KPG", valSgtg_W1KPG);
            params.put("Sgtg-W1GR", valSgtg_W1GR);
            params.put("Sgtg-W2KPG", valSgtg_W2KPG);
            params.put("Sgtg-W2GR", valSgtg_W2GR);
            params.put("Sgtg-W3KPG", valSgtg_W3KPG);
            params.put("Sgtg-W3GR", valSgtg_W3GR);
            params.put("Sgtg--KPG", valSgtg__KPG);
            params.put("Sgtg--GR", valSgtg__GR);
            params.put("OvalBRSBRW1KPG", valOvalBRSBRW1KPG);
            params.put("OvalBRSBRW1GR", valOvalBRSBRW1GR);
            params.put("OvalBRSBRW2KPG", valOvalBRSBRW2KPG);
            params.put("OvalBRSBRW2GR", valOvalBRSBRW2GR);
            params.put("OvalBRSBRW3KPG", valOvalBRSBRW3KPG);
            params.put("OvalBRSBRW3GR", valOvalBRSBRW3GR);
            params.put("OvalBRSBR-KPG", valOvalBRSBR_KPG);
            params.put("OvalBRSBR-GR", valOvalBRSBR_GR);
            params.put("OvalBRW1KPG", valOvalBRW1KPG);
            params.put("OvalBRW1GR", valOvalBRW1GR);
            params.put("OvalBRW2KPG", valOvalBRW2KPG);
            params.put("OvalBRW2GR", valOvalBRW2GR);
            params.put("OvalBRW3KPG", valOvalBRW3KPG);
            params.put("OvalBRW3GR", valOvalBRW3GR);
            params.put("OvalBR-KPG", valOvalBR_KPG);
            params.put("OvalBR-GR", valOvalBR_GR);
            params.put("OvalBSW1KPG", valOvalBSW1KPG);
            params.put("OvalBSW1GR", valOvalBSW1GR);
            params.put("OvalBSW2KPG", valOvalBSW2KPG);
            params.put("OvalBSW2GR", valOvalBSW2GR);
            params.put("OvalBSW3KPG", valOvalBSW3KPG);
            params.put("OvalBSW3GR", valOvalBSW3GR);
            params.put("OvalBS-KPG", valOvalBS_KPG);
            params.put("OvalBS-GR", valOvalBS_GR);
            params.put("OvalBBW1KPG", valOvalBBW1KPG);
            params.put("OvalBBW1GR", valOvalBBW1GR);
            params.put("OvalBBW2KPG", valOvalBBW2KPG);
            params.put("OvalBBW2GR", valOvalBBW2GR);
            params.put("OvalBBW3KPG", valOvalBBW3KPG);
            params.put("OvalBBW3GR", valOvalBBW3GR);
            params.put("OvalBB-KPG", valOvalBB_KPG);
            params.put("OvalBB-GR", valOvalBB_GR);
            params.put("OvalBB2W1KPG", valOvalBB2W1KPG);
            params.put("OvalBB2W1GR", valOvalBB2W1GR);
            params.put("OvalBB2W2KPG", valOvalBB2W2KPG);
            params.put("OvalBB2W2GR", valOvalBB2W2GR);
            params.put("OvalBB2W3KPG", valOvalBB2W3KPG);
            params.put("OvalBB2W3GR", valOvalBB2W3GR);
            params.put("OvalBB2-KPG", valOvalBB2_KPG);
            params.put("OvalBB2-GR", valOvalBB2_GR);
            params.put("Oval-W1KPG", valOval_W1KPG);
            params.put("Oval-W1GR", valOval_W1GR);
            params.put("Oval-W2KPG", valOval_W2KPG);
            params.put("Oval-W2GR", valOval_W2GR);
            params.put("Oval-W3KPG", valOval_W3KPG);
            params.put("Oval-W3GR", valOval_W3GR);
            params.put("Oval--KPG", valOval__KPG);
            params.put("Oval--GR", valOval__GR);
            params.put("PchLbgBRSBRW1KPG", valPchLbgBRSBRW1KPG);
            params.put("PchLbgBRSBRW1GR", valPchLbgBRSBRW1GR);
            params.put("PchLbgBRSBRW2KPG", valPchLbgBRSBRW2KPG);
            params.put("PchLbgBRSBRW2GR", valPchLbgBRSBRW2GR);
            params.put("PchLbgBRSBRW3KPG", valPchLbgBRSBRW3KPG);
            params.put("PchLbgBRSBRW3GR", valPchLbgBRSBRW3GR);
            params.put("PchLbgBRSBR-KPG", valPchLbgBRSBR_KPG);
            params.put("PchLbgBRSBR-GR", valPchLbgBRSBR_GR);
            params.put("PchLbgBRW1KPG", valPchLbgBRW1KPG);
            params.put("PchLbgBRW1GR", valPchLbgBRW1GR);
            params.put("PchLbgBRW2KPG", valPchLbgBRW2KPG);
            params.put("PchLbgBRW2GR", valPchLbgBRW2GR);
            params.put("PchLbgBRW3KPG", valPchLbgBRW3KPG);
            params.put("PchLbgBRW3GR", valPchLbgBRW3GR);
            params.put("PchLbgBR-KPG", valPchLbgBR_KPG);
            params.put("PchLbgBR-GR", valPchLbgBR_GR);
            params.put("PchLbgBSW1KPG", valPchLbgBSW1KPG);
            params.put("PchLbgBSW1GR", valPchLbgBSW1GR);
            params.put("PchLbgBSW2KPG", valPchLbgBSW2KPG);
            params.put("PchLbgBSW2GR", valPchLbgBSW2GR);
            params.put("PchLbgBSW3KPG", valPchLbgBSW3KPG);
            params.put("PchLbgBSW3GR", valPchLbgBSW3GR);
            params.put("PchLbgBS-KPG", valPchLbgBS_KPG);
            params.put("PchLbgBS-GR", valPchLbgBS_GR);
            params.put("PchLbgBBW1KPG", valPchLbgBBW1KPG);
            params.put("PchLbgBBW1GR", valPchLbgBBW1GR);
            params.put("PchLbgBBW2KPG", valPchLbgBBW2KPG);
            params.put("PchLbgBBW2GR", valPchLbgBBW2GR);
            params.put("PchLbgBBW3KPG", valPchLbgBBW3KPG);
            params.put("PchLbgBBW3GR", valPchLbgBBW3GR);
            params.put("PchLbgBB-KPG", valPchLbgBB_KPG);
            params.put("PchLbgBB-GR", valPchLbgBB_GR);
            params.put("PchLbgBB2W1KPG", valPchLbgBB2W1KPG);
            params.put("PchLbgBB2W1GR", valPchLbgBB2W1GR);
            params.put("PchLbgBB2W2KPG", valPchLbgBB2W2KPG);
            params.put("PchLbgBB2W2GR", valPchLbgBB2W2GR);
            params.put("PchLbgBB2W3KPG", valPchLbgBB2W3KPG);
            params.put("PchLbgBB2W3GR", valPchLbgBB2W3GR);
            params.put("PchLbgBB2-KPG", valPchLbgBB2_KPG);
            params.put("PchLbgBB2-GR", valPchLbgBB2_GR);
            params.put("PchLbg-W1KPG", valPchLbg_W1KPG);
            params.put("PchLbg-W1GR", valPchLbg_W1GR);
            params.put("PchLbg-W2KPG", valPchLbg_W2KPG);
            params.put("PchLbg-W2GR", valPchLbg_W2GR);
            params.put("PchLbg-W3KPG", valPchLbg_W3KPG);
            params.put("PchLbg-W3GR", valPchLbg_W3GR);
            params.put("PchLbg--KPG", valPchLbg__KPG);
            params.put("PchLbg--GR", valPchLbg__GR);
            params.put("TotalW1KPG", valTotalW1KPG);
            params.put("TotalW1GR", valTotalW1GR);
            params.put("TotalW2KPG", valTotalW2KPG);
            params.put("TotalW2GR", valTotalW2GR);
            params.put("TotalW3KPG", valTotalW3KPG);
            params.put("TotalW3GR", valTotalW3GR);
            params.put("Total-KPG", valTotal_KPG);
            params.put("Total-GR", valTotal_GR);
            params.put("TotalTotalKPG", valTotalTotalKPG);
            params.put("TotalTotalGR", valTotalTotalGR);
            params.put("Total1BRSBRKPG", valTotal1BRSBRKPG);
            params.put("Total1BRSBRGR", valTotal1BRSBRGR);
            params.put("Total1BRKPG", valTotal1BRKPG);
            params.put("Total1BRGR", valTotal1BRGR);
            params.put("Total1BSKPG", valTotal1BSKPG);
            params.put("Total1BSGR", valTotal1BSGR);
            params.put("Total1BBKPG", valTotal1BBKPG);
            params.put("Total1BBGR", valTotal1BBGR);
            params.put("Total1BB2KPG", valTotal1BB2KPG);
            params.put("Total1BB2GR", valTotal1BB2GR);
            params.put("Total1-KPG", new Long(0));
            params.put("Total1-GR", new Long(0));
            params.put("Total2BRSBRKPG", valTotal2BRSBRKPG);
            params.put("Total2BRSBRGR", valTotal2BRSBRGR);
            params.put("Total2BRKPG", valTotal2BRKPG);
            params.put("Total2BRGR", valTotal2BRGR);
            params.put("Total2BSKPG", valTotal2BSKPG);
            params.put("Total2BSGR", valTotal2BSGR);
            params.put("Total2BBKPG", valTotal2BBKPG);
            params.put("Total2BBGR", valTotal2BBGR);
            params.put("Total2BB2KPG", valTotal2BB2KPG);
            params.put("Total2BB2GR", valTotal2BB2GR);
            params.put("Total2-KPG", new Long(0));
            params.put("Total2-GR", new Long(0));
            params.put("Total3BRSBRKPG", valTotal3BRSBRKPG);
            params.put("Total3BRSBRGR", valTotal3BRSBRGR);
            params.put("Total3BRKPG", valTotal3BRKPG);
            params.put("Total3BRGR", valTotal3BRGR);
            params.put("Total3BSKPG", valTotal3BSKPG);
            params.put("Total3BSGR", valTotal3BSGR);
            params.put("Total3BBKPG", valTotal3BBKPG);
            params.put("Total3BBGR", valTotal3BBGR);
            params.put("Total3BB2KPG", valTotal3BB2KPG);
            params.put("Total3BB2GR", valTotal3BB2GR);
            params.put("Total3-KPG", new Long(0));
            params.put("Total3-GR", new Long(0));
            params.put("TotalMkLainBRSBRKPG", valTotalMkLainBRSBRKPG);
            params.put("TotalMkLainBRSBRGR", valTotalMkLainBRSBRGR);
            params.put("TotalMkLainBRKPG", valTotalMkLainBRKPG);
            params.put("TotalMkLainBRGR", valTotalMkLainBRGR);
            params.put("TotalMkLainBSKPG", valTotalMkLainBSKPG);
            params.put("TotalMkLainBSGR", valTotalMkLainBSGR);
            params.put("TotalMkLainBBKPG", valTotalMkLainBBKPG);
            params.put("TotalMkLainBBGR", valTotalMkLainBBGR);
            params.put("TotalMkLainBB2KPG", valTotalMkLainBB2KPG);
            params.put("TotalMkLainBB2GR", valTotalMkLainBB2GR);
            params.put("TotalMkLain-KPG", valTotalMkLain_KPG + valTotal1_KPG + valTotal2_KPG + valTotal3_KPG);
            params.put("TotalMkLain-GR", valTotalMkLain_GR + valTotal1_GR + valTotal2_GR + valTotal3_GR);
            params.put("TotalSgtgBRSBRKPG", valTotalSgtgBRSBRKPG);
            params.put("TotalSgtgBRSBRGR", valTotalSgtgBRSBRGR);
            params.put("TotalSgtgBRKPG", valTotalSgtgBRKPG);
            params.put("TotalSgtgBRGR", valTotalSgtgBRGR);
            params.put("TotalSgtgBSKPG", valTotalSgtgBSKPG);
            params.put("TotalSgtgBSGR", valTotalSgtgBSGR);
            params.put("TotalSgtgBBKPG", valTotalSgtgBBKPG);
            params.put("TotalSgtgBBGR", valTotalSgtgBBGR);
            params.put("TotalSgtgBB2KPG", valTotalSgtgBB2KPG);
            params.put("TotalSgtgBB2GR", valTotalSgtgBB2GR);
            params.put("TotalSgtg-KPG", valTotalSgtg_KPG);
            params.put("TotalSgtg-GR", valTotalSgtg_GR);
            params.put("TotalOvalBRSBRKPG", valTotalOvalBRSBRKPG);
            params.put("TotalOvalBRSBRGR", valTotalOvalBRSBRGR);
            params.put("TotalOvalBRKPG", valTotalOvalBRKPG);
            params.put("TotalOvalBRGR", valTotalOvalBRGR);
            params.put("TotalOvalBSKPG", valTotalOvalBSKPG);
            params.put("TotalOvalBSGR", valTotalOvalBSGR);
            params.put("TotalOvalBBKPG", valTotalOvalBBKPG);
            params.put("TotalOvalBBGR", valTotalOvalBBGR);
            params.put("TotalOvalBB2KPG", valTotalOvalBB2KPG);
            params.put("TotalOvalBB2GR", valTotalOvalBB2GR);
            params.put("TotalOval-KPG", valTotalOval_KPG);
            params.put("TotalOval-GR", valTotalOval_GR);
            params.put("TotalPchLbgBRSBRKPG", valTotalPchLbgBRSBRKPG);
            params.put("TotalPchLbgBRSBRGR", valTotalPchLbgBRSBRGR);
            params.put("TotalPchLbgBRKPG", valTotalPchLbgBRKPG);
            params.put("TotalPchLbgBRGR", valTotalPchLbgBRGR);
            params.put("TotalPchLbgBSKPG", valTotalPchLbgBSKPG);
            params.put("TotalPchLbgBSGR", valTotalPchLbgBSGR);
            params.put("TotalPchLbgBBKPG", valTotalPchLbgBBKPG);
            params.put("TotalPchLbgBBGR", valTotalPchLbgBBGR);
            params.put("TotalPchLbgBB2KPG", valTotalPchLbgBB2KPG);
            params.put("TotalPchLbgBB2GR", valTotalPchLbgBB2GR);
            params.put("TotalPchLbg-KPG", valTotalPchLbg_KPG);
            params.put("TotalPchLbg-GR", valTotalPchLbg_GR);
            params.put("-W1KPG", val_W1KPG);
            params.put("-W1GR", val_W1GR);
            params.put("-W2KPG", val_W2KPG);
            params.put("-W2GR", val_W2GR);
            params.put("-W3KPG", val_W3KPG);
            params.put("-W3GR", val_W3GR);
            params.put("--KPG", val__KPG);
            params.put("--GR", val__GR);
            params.put("-TotalKPG", val_TotalKPG);
            params.put("-TotalGR", val_TotalGR);
//            System.out.println("---------------------------mulai--------------------------");
            for (Map.Entry<String, Object> entry : params.entrySet()) {
//                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
            JasperPrint JASP_PRINT = JasperFillManager.fillReport(JASP_REP, params, Utility.db.getConnection());
//            JasperViewer jasperViewer = new JasperViewer(JASP_PRINT);
            JasperViewer.viewReport(JASP_PRINT, false);
            //jasperViewer.viewReport(JASP_PRINT, false);//isExitOnClose (false)

//            JDialog dialog = new JDialog(this);//the owner
//            dialog.setContentPane(jasperViewer.getContentPane());
//            dialog.setSize(jasperViewer.getSize());
//            dialog.setLocation(jasperViewer.getLocation());
//            dialog.setTitle(jasperViewer.getTitle());
//            /*dialog.setIconImage(Toolkit.getDefaultToolkit().getImage(
//                getClass().getResource("URL IMG")));*/
//            dialog.setVisible(true);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_button_printActionPerformed

    private void button_refresh_ketahananBakuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_refresh_ketahananBakuActionPerformed
        // TODO add your handling code here:
        Thread thread = new Thread() {
            @Override
            public void run() {
                refresh_TabelKetahanan();
                Refresh_BP_Unworkable();
                Refresh_grading_on_proses();
                JOptionPane.showMessageDialog(null, "Proses Selesai");
                this.stop();
                this.destroy();
            }
        };
        thread.start();
    }//GEN-LAST:event_button_refresh_ketahananBakuActionPerformed

    private void button_simpan_stok_terakhirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_simpan_stok_terakhirActionPerformed
        // TODO add your handling code here:
        if (MainForm.Login_idPegawai.equals("20180102221")) {
            try {
                Utility.db.getConnection().setAutoCommit(false);
                for (int i = 0; i < table_stok_per_kartu.getRowCount(); i++) {
                    if (table_stok_per_kartu.getValueAt(i, 13).toString().equals("0")) {
                        String Query = "UPDATE `tb_bahan_baku_masuk` SET \n"
                                + "`last_stok` = '0' \n"
                                + "WHERE `no_kartu_waleta` = '" + table_stok_per_kartu.getValueAt(i, 0).toString() + "'";
                        Utility.db.getStatement().executeUpdate(Query);
                    }
                }
                Utility.db.getConnection().commit();
                JOptionPane.showMessageDialog(this, "Data Berhasil disimpan");
            } catch (Exception ex) {
                try {
                    Utility.db.getConnection().rollback();
                } catch (SQLException ex1) {
                    Logger.getLogger(JPanel_DataBahanBaku.class.getName()).log(Level.SEVERE, null, ex1);
                }
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    Utility.db.getConnection().setAutoCommit(true);
                } catch (SQLException ex) {
                    Logger.getLogger(JPanel_DataBahanBaku.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Maaf fungsi ini tidak boleh digunakan");
        }
    }//GEN-LAST:event_button_simpan_stok_terakhirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CheckBox_show_All_Cards;
    private javax.swing.JCheckBox CheckBox_show_dataMasihSisa;
    private javax.swing.JComboBox<String> ComboBox_FilterBentuk;
    private javax.swing.JComboBox<String> ComboBox_FilterBulu;
    private javax.swing.JComboBox<String> ComboBox_FilterKategori;
    private javax.swing.JComboBox<String> ComboBox_FilterWarna;
    private javax.swing.JComboBox<String> ComboBox_Search_grade;
    private com.toedter.calendar.JDateChooser Date_FilterStok;
    private com.toedter.calendar.JDateChooser Date_LP1;
    private com.toedter.calendar.JDateChooser Date_LP2;
    private com.toedter.calendar.JDateChooser Date_filter1;
    private com.toedter.calendar.JDateChooser Date_filter2;
    public static javax.swing.JTable Table_stock_bahan_baku;
    private javax.swing.JButton button_export;
    private javax.swing.JButton button_filter_LP;
    private javax.swing.JButton button_print;
    private javax.swing.JButton button_refresh_ketahananBaku;
    private javax.swing.JButton button_search;
    private javax.swing.JButton button_search_data_bahan_baku;
    private javax.swing.JButton button_search_stock;
    private javax.swing.JButton button_simpan_stok_terakhir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel_Data_KartuCMP;
    private javax.swing.JPanel jPanel_Data_Keluar;
    private javax.swing.JPanel jPanel_Data_LP;
    private javax.swing.JPanel jPanel_ketahanan_stok;
    private javax.swing.JPanel jPanel_stok_grade;
    private javax.swing.JPanel jPanel_stok_kartu;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel label_berat_cmp;
    private javax.swing.JLabel label_berat_keluar;
    private javax.swing.JLabel label_berat_lp;
    private javax.swing.JLabel label_grade;
    private javax.swing.JLabel label_keping_cmp;
    private javax.swing.JLabel label_keping_keluar;
    private javax.swing.JLabel label_keping_lp;
    private javax.swing.JLabel label_no_kartu;
    private javax.swing.JLabel label_total_cmp;
    private javax.swing.JLabel label_total_keluar;
    private javax.swing.JLabel label_total_lp;
    private javax.swing.JLabel label_total_sisa_gram;
    private javax.swing.JLabel label_total_sisa_kpg;
    private javax.swing.JLabel label_total_stok_gram;
    private javax.swing.JLabel label_total_stok_kpg;
    private javax.swing.JTable tabel_KartuMasuk;
    private javax.swing.JTable table_ketahanan_stok_waleta;
    private javax.swing.JTable table_on_proses;
    private javax.swing.JTable table_stok_for_bp;
    private javax.swing.JTable table_stok_for_jual;
    private javax.swing.JTable table_stok_for_sub;
    private javax.swing.JTable table_stok_for_waleta;
    private javax.swing.JTable table_stok_per_grade;
    private javax.swing.JTable table_stok_per_kartu;
    private javax.swing.JTable tb_LP;
    private javax.swing.JTable tb_cmp;
    private javax.swing.JTable tb_keluar;
    private javax.swing.JLabel txt_berat_LP;
    private javax.swing.JLabel txt_berat_jual;
    private javax.swing.JLabel txt_berat_masuk;
    private javax.swing.JLabel txt_berat_sisa;
    private javax.swing.JLabel txt_ketahanan_sub;
    private javax.swing.JLabel txt_kpg_LP;
    private javax.swing.JLabel txt_kpg_jual;
    private javax.swing.JLabel txt_kpg_masuk;
    private javax.swing.JLabel txt_kpg_sisa;
    private javax.swing.JLabel txt_pengeluaran_sub;
    private javax.swing.JTextField txt_search_data_bahan_baku;
    private javax.swing.JLabel txt_stok_bp;
    private javax.swing.JLabel txt_stok_bp1;
    private javax.swing.JLabel txt_stok_sub;
    private javax.swing.JLabel txt_stok_sub1;
    private javax.swing.JLabel txt_stok_unworkable;
    private javax.swing.JLabel txt_stok_unworkable1;
    private javax.swing.JLabel txt_stok_waleta;
    private javax.swing.JLabel txt_tanggal_end_sub;
    private javax.swing.JLabel txt_tanggal_end_waleta;
    private javax.swing.JLabel txt_total_proses_grading;
    // End of variables declaration//GEN-END:variables

}
