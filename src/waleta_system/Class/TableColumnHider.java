package waleta_system.Class;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class TableColumnHider {
    private final JTable table;
    private final TableColumnModel ColumnModel;
    private final Map hiddenColumns;
    
    public TableColumnHider(JTable table) {
        this.table = table;
        ColumnModel = table.getColumnModel();
        hiddenColumns = new HashMap();
    }

    public void hide(String columnName) {
        int index = ColumnModel.getColumnIndex(columnName);
        TableColumn column = ColumnModel.getColumn(index);
        hiddenColumns.put(columnName, column);
        hiddenColumns.put(":" + columnName, index);
        ColumnModel.removeColumn(column);
    }

    public void show(String columnName) {
        Object o = hiddenColumns.remove(columnName);
        if (o == null) {
            return;
        }
        ColumnModel.addColumn((TableColumn) o);
        o = hiddenColumns.remove(":" + columnName);
        if (o == null) {
            return;
        }
        int column = ((Integer) o).intValue();
        int lastColumn = ColumnModel.getColumnCount() - 1;
        if (column < lastColumn) {
            ColumnModel.moveColumn(lastColumn, column);
        }
    }
}
