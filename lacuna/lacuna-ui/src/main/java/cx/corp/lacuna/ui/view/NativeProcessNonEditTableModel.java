package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.domain.NativeProcess;

import javax.swing.table.DefaultTableModel;

public class NativeProcessNonEditTableModel extends DefaultTableModel {

    private static final Class<?>[] NATIVEPROCESS_FIELD_TYPES = {Integer.class, String.class, String.class};

    public void addRow(NativeProcess process) {
        super.addRow(processToCells(process));
    }

    private Object[] processToCells(NativeProcess process) {
        return new Object[]{
            process.getPid(),
            process.getOwner(),
            process.getDescription()
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return
            columnIndex >= NATIVEPROCESS_FIELD_TYPES.length
                ? null
                : NATIVEPROCESS_FIELD_TYPES[columnIndex];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // make the table read-only
        return false;
    }
}
