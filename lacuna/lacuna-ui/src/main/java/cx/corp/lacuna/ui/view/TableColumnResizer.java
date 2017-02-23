package cx.corp.lacuna.ui.view;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/*
* Adapted from https://tips4java.wordpress.com/2008/11/10/table-column-adjuster/
* original by Rob Camick in 2008
*
* "We assume no responsibility for the code. You are free to use and/or modify
* and/or distribute any or all code posted on the Java Tips Weblog without
* restriction. A credit in the code comments would be nice, but not in any way
* mandatory."
*/

class TableColumnResizer {
    private final JTable table;

    public TableColumnResizer(JTable table) {
        this.table = table;
    }

    public void resize() {
        disableAutoResize();
        resizeColumns();
    }

    private void disableAutoResize() {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    private void resizeColumns() {
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = getColumn(i);
            findAndSetSuitableWidth(i, column);
        }
    }

    private TableColumn getColumn(int columnIndex) {
        return table.getColumnModel().getColumn(columnIndex);
    }

    private void findAndSetSuitableWidth(int columnIndex, TableColumn column) {
        int columnWidth = findSuitableWidthForColumn(
            columnIndex,
            column.getMinWidth(),
            column.getMaxWidth()
        );
        column.setPreferredWidth(columnWidth);
    }

    private int findSuitableWidthForColumn(int columnIndex, int minWidth, int maxWidth) {
        int largestWidth = minWidth;

        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer renderer = table.getCellRenderer(row, columnIndex);
            Component c = table.prepareRenderer(renderer, row, columnIndex);
            int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
            largestWidth = Math.max(largestWidth, width);

            //  We've exceeded the maximum width, no need to check other rows
            if (largestWidth >= maxWidth) {
                largestWidth = maxWidth;
                break;
            }
        }

        return largestWidth;
    }
}