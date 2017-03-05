package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.MemoryReader;
import cx.corp.lacuna.core.MemoryWriter;
import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.ui.CoreDataType;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataInspectorComponent {

    private static final int MAX_STRING_CHARS = 512;
    private static final int DATATYPE_NAME_COLUMN = 0;
    private static final int VALUE_COLUMN = 1;

    private final MemoryReader reader;
    private final MemoryWriter writer;

    private AtomicBoolean wasProgrammaticUpdate = new AtomicBoolean(false);
    private NativeProcess activeProcess;
    private int dataOffset;
    private JPanel panel;
    private JTable table;

    public DataInspectorComponent(MemoryReader reader, MemoryWriter writer) {
        Objects.requireNonNull(reader, "reader cannot be null!");
        Objects.requireNonNull(writer, "writer cannot be null!");
        this.reader = reader;
        this.writer = writer;
        createComponent();
    }

    public void setDataOffset(int dataOffset) {
        this.dataOffset = dataOffset;
        updateTableFromMemory();
    }

    public void setActiveProcess(NativeProcess process) {
        this.activeProcess = process;
        updateTableFromMemory();
    }

    public JPanel getPanel() {
        return panel;
    }

    private void createComponent() {
        table = new JTable();

        DefaultTableModel inspectorTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != DATATYPE_NAME_COLUMN && super.isCellEditable(row, column);
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        inspectorTableModel.addColumn("Type");
        inspectorTableModel.addColumn("Value");
        for (CoreDataType type : CoreDataType.values()) {
            inspectorTableModel.addRow(new Object[]{type.getHumanReadableName(), type.getDefaultValue()});
        }
        inspectorTableModel.addTableModelListener(e -> {
            int column = e.getColumn();
            int row = e.getFirstRow();

            if (column != VALUE_COLUMN
                || row == TableModelEvent.HEADER_ROW
                || e.getType() != TableModelEvent.UPDATE) {
                return;
            }

            if (wasProgrammaticUpdate.get()) {
                wasProgrammaticUpdate.set(false);
            } else {
                // if we update the values in the table, we don't want
                // to re-write those values back.
                persistNewValueOnRow(row);
            }
        });

        table.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        table.setModel(inspectorTableModel);
        table.putClientProperty("terminateEditOnFocusLost", true);
        table.setCellEditor(table.getCellEditor());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int selectedRow = target.getSelectedRow();
                int selectedColumn = target.getSelectedColumn();
                if (selectedColumn == DATATYPE_NAME_COLUMN) {
                    return;
                }
                target.editCellAt(selectedRow, selectedColumn);
                target.setSurrendersFocusOnKeystroke(true);
                target.getEditorComponent().requestFocus();
            }
        });
        table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(table);
        panel = new JPanel(new BorderLayout());
        panel.add(scroll, BorderLayout.CENTER);
    }

    private void persistNewValueOnRow(int row) {
        if (activeProcess == null) {
            return;
        }
        CoreDataType type = getRowType(row);
        Object newValue = table.getValueAt(row, VALUE_COLUMN);
        persistValue(type, newValue);
    }

    private void persistValue(CoreDataType type, Object newValue) {
        try {
            String strValue = (String) newValue;
            switch (type) {
                case BOOLEAN:
                    writer.writeBoolean(
                        activeProcess,
                        dataOffset,
                        strValue.equalsIgnoreCase("true") || strValue.equals("1")
                    );
                    break;
                case BYTE:
                    writer.writeByte(
                        activeProcess,
                        dataOffset,
                        Byte.parseByte(strValue)
                    );
                    break;
                case CHAR_UTF8:
                    writer.writeCharUTF8(
                        activeProcess,
                        dataOffset,
                        strValue.charAt(0)
                    );
                    break;
                case CHAR_UTF16:
                    writer.writeCharUTF16LE(
                        activeProcess,
                        dataOffset,
                        strValue.charAt(0)
                    );
                    break;
                case SHORT:
                    writer.writeShort(
                        activeProcess,
                        dataOffset,
                        Short.parseShort(strValue)
                    );
                    break;
                case INT:
                    writer.writeInt(
                        activeProcess,
                        dataOffset,
                        Integer.parseInt(strValue)
                    );
                    break;
                case LONG:
                    writer.writeLong(
                        activeProcess,
                        dataOffset,
                        Long.parseLong(strValue)
                    );
                    break;
                case FLOAT:
                    writer.writeFloat(
                        activeProcess,
                        dataOffset,
                        Float.parseFloat(strValue)
                    );
                    break;
                case DOUBLE:
                    writer.writeDouble(
                        activeProcess,
                        dataOffset,
                        Double.parseDouble(strValue)
                    );
                    break;
                case STRING_UTF8:
                    writer.writeStringUTF8(
                        activeProcess,
                        dataOffset,
                        strValue
                    );
                    break;
                case STRING_UTF16:
                    writer.writeStringUTF16LE(
                        activeProcess,
                        dataOffset,
                        strValue
                    );
                    break;
            }
        } catch (Exception ex) {
        }
    }

    private void updateTableFromMemory() {
        if (activeProcess == null) {
            return;
        }

        for (int i = 0; i < table.getModel().getRowCount(); i++) {
            Object value = readValueAt(i);
            if (value != null) {
                wasProgrammaticUpdate.set(true);
                table.getModel().setValueAt(value, i, VALUE_COLUMN);
            }
        }
    }

    private Object readValueAt(int row) {
        CoreDataType type = getRowType(row);
        if (type == null) {
            return null;
        }
        return readFromMemory(type);
    }

    private Object readFromMemory(CoreDataType type) {
        try {
            switch (type) {
                case BOOLEAN: return reader.readBoolean(activeProcess, dataOffset);
                case BYTE: return reader.readByte(activeProcess, dataOffset);
                case CHAR_UTF8: return reader.readCharUTF8(activeProcess, dataOffset);
                case CHAR_UTF16: return reader.readCharUTF16LE(activeProcess, dataOffset);
                case SHORT: return reader.readShort(activeProcess, dataOffset);
                case INT: return reader.readInt(activeProcess, dataOffset);
                case LONG: return reader.readLong(activeProcess, dataOffset);
                case FLOAT: return reader.readFloat(activeProcess, dataOffset);
                case DOUBLE: return reader.readDouble(activeProcess, dataOffset);
                case STRING_UTF8: return reader.readStringUTF8(activeProcess, dataOffset, MAX_STRING_CHARS);
                case STRING_UTF16: return reader.readStringUTF16LE(activeProcess, dataOffset, MAX_STRING_CHARS);
            }
        } catch (Exception ex) {
        }
        return null;
    }

    private CoreDataType getRowType(int row) {
        String typeHumanReadableName = getTypeColumnValue(row);
        return CoreDataType.fromHumanReadableName(typeHumanReadableName);
    }

    private String getTypeColumnValue(int modelRow) {
        return (String) table.getModel().getValueAt(modelRow, DATATYPE_NAME_COLUMN);
    }
}
