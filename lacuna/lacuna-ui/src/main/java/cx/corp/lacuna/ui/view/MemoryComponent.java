package cx.corp.lacuna.ui.view;

import org.exbin.deltahex.ScrollBarVisibility;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.utils.binary_data.EditableBinaryData;

import javax.swing.*;
import java.awt.*;

public class MemoryComponent {

    private static final long BYTES_PER_ROW = 16;

    private final EditableBinaryData memoryProvider;
    private CodeArea codeArea;
    private JPanel panel;

    public MemoryComponent(EditableBinaryData memoryProvider) {
        this.memoryProvider = memoryProvider;
        createComponent();
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
        JMenuItem gotoItem = new JMenuItem("Go to address");
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
                codeArea.setCaretPosition(offsetLong);
                codeArea.getScrollPosition().setScrollLinePosition(offsetLong / BYTES_PER_ROW);
            } catch (NumberFormatException ex) {
            }
        });

        popupMenu.add(gotoItem);
        return popupMenu;
    }

    public JPanel getPanel() {
        return panel;
    }
}
