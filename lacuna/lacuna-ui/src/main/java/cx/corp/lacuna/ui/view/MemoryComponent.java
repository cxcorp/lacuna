package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.LacunaBootstrap;
import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.domain.NativeProcess;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.EditableBinaryData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MemoryComponent {

    private static final long BYTES_PER_ROW = 16;

    private final EditableBinaryData memoryProvider;
    private CodeArea codeArea;
    private NativeProcess activeProcess;
    private JPanel panel;

    public MemoryComponent(EditableBinaryData memoryProvider) {
        this.memoryProvider = memoryProvider;
        createComponent();
    }

    private void createComponent() {
        panel = new JPanel(new BorderLayout());
        codeArea = new CodeArea();
        codeArea.setPreferredSize(new Dimension(300, 300));
        codeArea.setData(memoryProvider);
        codeArea.setComponentPopupMenu(createPopupMenu());
        panel.add(codeArea, BorderLayout.CENTER);
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem gotoItem = new JMenuItem("Go to address");
        gotoItem.setAccelerator(
            KeyStroke.getKeyStroke(
                KeyEvent.VK_G,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
            )
        );
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
