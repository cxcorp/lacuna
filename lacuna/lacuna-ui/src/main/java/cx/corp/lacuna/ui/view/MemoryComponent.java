package cx.corp.lacuna.ui.view;

import org.apache.commons.lang3.StringUtils;
import org.exbin.deltahex.CaretMovedListener;
import org.exbin.deltahex.CaretPosition;
import org.exbin.deltahex.CodeType;
import org.exbin.deltahex.ScrollBarVisibility;
import org.exbin.deltahex.Section;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.utils.binary_data.EditableBinaryData;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MemoryComponent {

    private final EditableBinaryData memoryProvider;
    private CodeArea codeArea;
    private JPanel panel;

    public MemoryComponent(EditableBinaryData memoryProvider) {
        Objects.requireNonNull(memoryProvider, "memoryProvider cannot be null!");
        this.memoryProvider = memoryProvider;
        createComponent();
    }

    public JPanel getPanel() {
        return panel;
    }

    /**
     * Notifies that the {@code BinaryData} memory provider has been updated,
     * e.g. a new process has been selected. This method should be called
     * <em>after</em> the provider has been updated to have the component
     * recalculate scrollbars etc.
     */
    public void notifyProviderUpdated() {
        // call computePaintData to make the CodeArea recalculate
        // the vertical scrollbar
        codeArea.computePaintData();
    }

    public void addCaretMovedListener(CaretMovedListener listener) {
        codeArea.addCaretMovedListener(listener);
    }

    private void createComponent() {
        panel = new JPanel(new BorderLayout());
        codeArea = new CodeArea();
        codeArea.setPreferredSize(new Dimension(300, 300));
        codeArea.setData(memoryProvider);
        codeArea.setComponentPopupMenu(createPopupMenu());
        codeArea.setVerticalScrollBarVisibility(ScrollBarVisibility.ALWAYS);
        codeArea.setHorizontalScrollBarVisibility(ScrollBarVisibility.IF_NEEDED);
        panel.add(codeArea, BorderLayout.CENTER);
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(createGoToAddressMenuItem());
        popupMenu.add(new JSeparator());
        popupMenu.add(createChangeBaseMenu());
        return popupMenu;
    }

    private JMenuItem createGoToAddressMenuItem() {
        JMenuItem gotoItem = new JMenuItem("Go To Address");
        gotoItem.addActionListener(e -> {
            String result = JOptionPane.showInputDialog(
                codeArea,
                "Enter address to go to (hexadecimal):",
                "Go to address",
                JOptionPane.PLAIN_MESSAGE);
            try {
                Integer offset = Integer.parseInt(result, 16);
                long offsetLong = offset & 0xFFFFFFFFL;
                if (offsetLong < 0) {
                    return;
                }
                codeArea.setCaretPosition(offsetLong, 0);
                codeArea.revealCursor();
            } catch (NumberFormatException ex) {
            }
        });
        return gotoItem;
    }

    private JMenuItem createChangeBaseMenu() {
        JMenu menu = new JMenu("Change Base");
        addBaseMenuItems(menu);
        return menu;
    }

    private void addBaseMenuItems(JMenu menu) {
        for (CodeType type : CodeType.values()) {
            String label = getCapitalizedName(type);
            JMenuItem item = new JMenuItem(label);
            onActionChangeCodeAreaCodeType(item);
            menu.add(item);
        }
    }

    private static String getCapitalizedName(CodeType type) {
        return StringUtils.capitalize(type.name().toLowerCase());
    }

    private void onActionChangeCodeAreaCodeType(JMenuItem item) {
        item.addActionListener(e -> {
            String typeName = e.getActionCommand().toUpperCase();
            CodeType requestedType = CodeType.valueOf(typeName);
            codeArea.setCodeType(requestedType);
        });
    }
}
