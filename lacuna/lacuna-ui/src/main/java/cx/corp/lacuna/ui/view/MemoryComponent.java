package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.LacunaBootstrap;
import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.domain.NativeProcess;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.EditableBinaryData;

import javax.swing.*;
import java.awt.*;

public class MemoryComponent {

    private final EditableBinaryData memoryProvider;
    private NativeProcess activeProcess;
    private JPanel panel;

    public MemoryComponent(EditableBinaryData memoryProvider) {
        this.memoryProvider = memoryProvider;
        createComponent();
    }

    private void createComponent() {
        panel = new JPanel(new BorderLayout());
        CodeArea codeArea = new CodeArea();
        codeArea.setPreferredSize(new Dimension(300, 300));
        LacunaBootstrap b = LacunaBootstrap.forCurrentPlatform();
        codeArea.setData(memoryProvider);
        panel.add(codeArea, BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return panel;
    }
}
