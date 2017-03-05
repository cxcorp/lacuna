package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.domain.NativeProcess;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class NativeProcessNonEditTableModel extends DefaultTableModel {

    private static final Class<?>[] NATIVEPROCESS_FIELD_TYPES = {Integer.class, String.class, String.class};
    private final List<NativeProcess> processes = new ArrayList<>();

    public void addRow(NativeProcess process) {
        processes.add(process);
        super.addRow(processToCells(process));
    }

    public void clearRows() {
        super.setRowCount(0);
    }

    public NativeProcess getProcess(int modelIndex) {
        return processes.get(modelIndex);
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
