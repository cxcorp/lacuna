package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.ui.presenter.MainCallbacks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Observable;

public class MainWindow extends Observable implements MainView{

    private static final String TITLE = "Lacuna";
    private static final String NO_PROCESS_SELECTED_TEXT = "No process selected";
    private final ChooseProcessDialog chooseProcessDialog;
    private MainCallbacks callbacks;
    private NativeProcess activeProcess;

    private JFrame frame;
    private JLabel activeProcessLabel;
    private JPanel memoryPanelParent;
    private JPanel memoryPanel;

    public MainWindow(ChooseProcessDialog chooseProcDialog) {
        this.chooseProcessDialog = chooseProcDialog;
        createWindow();
    }

    public void show() {
        frame.setVisible(true);
    }

    /**
     * Sets the drawn memory component panel, removing the old panel.
     * If {@code panel} is {@code null}, just removes the current panel
     * if it exists.
     * @param panel Panel to display as the memory component panel, or
     *              {@code null} if the current panel should be removed.
     */
    public void setMemoryPanel(JPanel panel) {
        if (memoryPanel != null) {
            removeOldMemoryPanel();
        }
        if (panel != null) {
            memoryPanelParent.add(panel, BorderLayout.CENTER);
        }
        memoryPanel = panel;
    }

    private void removeOldMemoryPanel() {
        memoryPanelParent.remove(memoryPanel);
    }

    //<editor-fold desc="UI creation">
    private void createWindow() {
        createFrame();
        createMenuBar();
        createComponents();
        frame.pack();
    }

    private void createFrame() {
        frame = new JFrame(TITLE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(600, 400));
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem chooseProcessItem = new JMenuItem("Choose process...");
        chooseProcessItem.setMnemonic(KeyEvent.VK_O);
        chooseProcessItem.setAccelerator( // CTRL+o
            KeyStroke.getKeyStroke(
                KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
                // CTRL on windows, CMD on mac, etc.
            )
        );
        chooseProcessItem.addActionListener(e -> {
            chooseProcessDialog.showDialogWithCallback(
                this.frame,
                newProc -> {
                    setActiveProcess(newProc);
                    if (callbacks != null) {
                        callbacks.newActiveProcessSelected();
                    }
                }
            );
        });
        fileMenu.add(chooseProcessItem);

        JSeparator separator = new JSeparator();
        fileMenu.add(separator);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            // Dispatch the same event that pressing on the window chrome's X button
            // would
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        });
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
    }

    private void createComponents() {
        JPanel contents = new JPanel(new BorderLayout());

        activeProcessLabel = new JLabel("No process selected");
        contents.add(activeProcessLabel, BorderLayout.NORTH);

        memoryPanelParent = new JPanel(new BorderLayout());
        contents.add(memoryPanelParent, BorderLayout.CENTER);

        frame.setContentPane(contents);
    }
    //</editor-fold>

    @Override
    public void attach(MainCallbacks mainCallbacks) {
        this.callbacks = mainCallbacks;
    }

    @Override
    public void setActiveProcess(NativeProcess newProcess) {
        activeProcess = newProcess;
        if (newProcess == null) {
            activeProcessLabel.setText(NO_PROCESS_SELECTED_TEXT);
            frame.setTitle(TITLE);
        } else {
            activeProcessLabel.setText(newProcess.toString());
            frame.setTitle(String.format("%s - %s", newProcess.getDescription(), TITLE));
        }
    }

    @Override
    public NativeProcess getActiveProcess() {
        return activeProcess;
    }

    public void refresh() {
        frame.validate();
        frame.repaint();
    }
}
