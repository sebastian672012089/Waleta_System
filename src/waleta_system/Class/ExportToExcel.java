/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package waleta_system.Class;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import waleta_system.HRD.JPanel_Data_Karyawan;

/**
 *
 * @author PT. Waleta new PC 1
 */
public class ExportToExcel {
    
    public static Object[] add(Object[] arr, Object... elements){
        Object[] tempArr = new Object[arr.length+elements.length];
        System.arraycopy(arr, 0, tempArr, 0, arr.length);
        
        for(int i=0; i < elements.length; i++)
            tempArr[arr.length+i] = elements[i];
        return tempArr;
        
    }
    
    public static void writeToExcel(DefaultTableModel tabelModel, JPanel panel){
        DefaultTableModel dm = tabelModel;
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet ws = wb.createSheet();
        
        //LOAD DATA TO TREE MAP
        TreeMap<String, Object[]> data = new TreeMap<>();
        
        //ADD COLUMN HEADER
        Object[] header = new Object[]{dm.getColumnName(0)};
        for (int i = 1; i < dm.getColumnCount(); i++) {
            header = add(header, dm.getColumnName(i));
        }
        data.put("-1", header);
        
        //ADD ROW AND CELLS
//        for (int i = 0; i < dm.getRowCount(); i++) {
//            data.put(Integer.toString(i), new Object[]{getCellValue(i, 0),getCellValue(i, 1),getCellValue(i, 2),getCellValue(i, 3),getCellValue(i, 4),getCellValue(i, 5),getCellValue(i, 6),getCellValue(i, 7),getCellValue(i, 8)});
//        }
        for (int i = 0; i < dm.getRowCount(); i++) {
            String column0_data = "";
            if (dm.getValueAt(i, 0) != null) {
                column0_data = dm.getValueAt(i, 0).toString();
            }
            Object[] rowData = new Object[]{column0_data};
            for (int x = 1; x < dm.getColumnCount(); x++) {
                String dataTabel = "";
                if (dm.getValueAt(i, x) != null) {
                    dataTabel = dm.getValueAt(i, x).toString();
                }
                rowData = add(rowData, dataTabel);
            }
            data.put(Integer.toString(i), rowData);
        }
        
        //WRITE TO EXCEL
        Set<String> ids = data.keySet();
        XSSFRow row;
        int rowID = 0;
        
        for (String key : ids) {
            row = ws.createRow(rowID++);
            
            //GET DATA AS PER KEY
            Object[] values = data.get(key);
            int cellID = 0;
            for (Object o : values) {
                Cell cell = row.createCell(cellID++);
                cell.setCellValue(o.toString());
            }
        }
        
        //WRITE TO FILE SYSTEM
        try {
            String FileName = null;
            final JFileChooser fc = new JFileChooser();
//            FileNameExtensionFilter excel = new FileNameExtensionFilter(".xlsx");
//            fc.setFileFilter(excel);
            int result = fc.showSaveDialog(panel);
            if (result == JFileChooser.APPROVE_OPTION) {
                FileName = fc.getSelectedFile().toString();
                if (FileName.substring(FileName.length()-5).equals(".xlsx")) {
                    FileName = FileName.substring(0, FileName.length()-5);
                }
            }
            System.out.println("FileName : " + FileName);
            FileOutputStream fos = new FileOutputStream(new File(FileName+".xlsx"));
            wb.write(fos);
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void writeToExcel(Object[] header, List<Object[]> arr, JPanel panel){
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet ws = wb.createSheet();
        
        //LOAD DATA TO TREE MAP
        TreeMap<String, Object[]> data = new TreeMap<>();
        data.put("-1", header);
        
        //ADD ROW AND CELLS
//        for (int i = 0; i < dm.getRowCount(); i++) {
//            data.put(Integer.toString(i), new Object[]{getCellValue(i, 0),getCellValue(i, 1),getCellValue(i, 2),getCellValue(i, 3),getCellValue(i, 4),getCellValue(i, 5),getCellValue(i, 6),getCellValue(i, 7),getCellValue(i, 8)});
//        }
        for (int i = 0; i < arr.size(); i++) {
            System.out.println("data-"+i+" = "+arr.get(i));
            data.put(Integer.toString(i), arr.get(i));
        }
        
        //WRITE TO EXCEL
        Set<String> ids = data.keySet();
        XSSFRow row;
        int rowID = 0;
        
        for (String key : ids) {
            row = ws.createRow(rowID++);
            
            //GET DATA AS PER KEY
            Object[] values = data.get(key);
            int cellID = 0;
            for (Object o : values) {
                Cell cell = row.createCell(cellID++);
                cell.setCellValue(o.toString());
            }
        }
        
        //WRITE TO FILE SYSTEM
        try {
            String FileName = null;
            final JFileChooser fc = new JFileChooser();
//            FileNameExtensionFilter excel = new FileNameExtensionFilter(".xlsx");
//            fc.setFileFilter(excel);
            int result = fc.showSaveDialog(panel);
            if (result == JFileChooser.APPROVE_OPTION) {
                FileName = fc.getSelectedFile().toString();
                if (FileName.substring(FileName.length()-5).equals(".xlsx")) {
                    FileName = FileName.substring(0, FileName.length()-5);
                }
            }
            System.out.println("FileName : " + FileName);
            FileOutputStream fos = new FileOutputStream(new File(FileName+".xlsx"));
            wb.write(fos);
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(JPanel_Data_Karyawan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
