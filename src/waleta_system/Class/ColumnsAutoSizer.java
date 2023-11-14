package waleta_system.Class;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Component;
import java.awt.FontMetrics;

public class ColumnsAutoSizer {

    public static void sizeColumnsToFit(JTable table) {
        sizeColumnsToFit(table, 10);
    }

    public static void sizeColumnsToFit(final JTable table, final int columnMargin) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                JTableHeader tableHeader = table.getTableHeader();

                if (tableHeader == null) {
                    // can't auto size a table without a header
                    return;
                }

                FontMetrics headerFontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());
                int[] maxWidths = new int[table.getColumnCount()];
                for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
                    int headerWidth = headerFontMetrics.stringWidth(table.getColumnName(columnIndex));

                    int maxWidth = getMaximalRequiredColumnWidth(table, columnIndex, headerWidth);

                    maxWidths[columnIndex] = maxWidth + columnMargin;
                }

                for (int i = 0; i < maxWidths.length; i++) {
                    table.getColumnModel().getColumn(i).setPreferredWidth(maxWidths[i]);
                }
            }
        });
    }

    private static int getMaximalRequiredColumnWidth(JTable table, int columnIndex, int headerWidth) {
        int maxWidth = headerWidth;

        TableColumn column = table.getColumnModel().getColumn(columnIndex);

        TableCellRenderer cellRenderer = column.getCellRenderer();

        if (cellRenderer == null) {
            cellRenderer = new DefaultTableCellRenderer();
        }

        for (int row = 0; row < table.getModel().getRowCount(); row++) {
            Component rendererComponent = cellRenderer.getTableCellRendererComponent(table,
                    table.getModel().getValueAt(row, columnIndex),
                    false,
                    false,
                    row,
                    columnIndex);

            double valueWidth = rendererComponent.getPreferredSize().getWidth();

            maxWidth = (int) Math.max(maxWidth, valueWidth);
        }

        return maxWidth;
    }
}
