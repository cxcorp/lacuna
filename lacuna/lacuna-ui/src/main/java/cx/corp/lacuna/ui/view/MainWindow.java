package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.ui.presenter.MainCallbacks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Observable;

public class MainWindow extends Observable implements MainView {

    private static final String TITLE = "Lacuna";
    private static final String NO_PROCESS_SELECTED_TEXT = "No process selected";
    private final ChooseProcessDialog chooseProcessDialog;
    private MainCallbacks callbacks;
    private NativeProcess activeProcess;

    private JFrame frame;
    private JLabel activeProcessLabel;
    private JPanel memoryPanel;
    private JPanel memoryPanelParent;
    private JPanel bookmarkPanel;
    private JPanel bookmarkPanelParent;
    private JPanel dataInspectorPanel;
    private JPanel dataInspectorPanelParent;

    public MainWindow(ChooseProcessDialog chooseProcDialog) {
        this.chooseProcessDialog = chooseProcDialog;
        createWindow();
    }

    public void show() {
        frame.setVisible(true);
    }

    /**
     * Sets the drawn memory component panel, removing the old panel.
     * If {@code newPanel} is {@code null}, just removes the current panel
     * if it exists.
     *
     * @param newPanel Panel to display as the memory component panel, or
     *                 {@code null} if the current panel should be removed.
     */
    public void setMemoryPanel(JPanel newPanel) {
        if (memoryPanel != null) {
            removeOldMemoryPanel();
        }
        if (newPanel != null) {
            memoryPanelParent.add(newPanel, BorderLayout.CENTER);
        }
        memoryPanel = newPanel;
    }

    public void setBookmarkPanel(JPanel newPanel) {
        if (bookmarkPanel != null) {
            removeOldBookmarkPanel();
        }
        if (newPanel != null) {
            bookmarkPanelParent.add(newPanel, BorderLayout.CENTER);
        }
        bookmarkPanel = newPanel;
    }

    public void setDataInspectorPanel(JPanel newPanel) {
        if (dataInspectorPanel != null) {
            removeOldDataInspectorPanel();
        }
        if (newPanel != null) {
            dataInspectorPanelParent.add(newPanel, BorderLayout.CENTER);
        }
        dataInspectorPanel = newPanel;
    }

    private void removeOldMemoryPanel() {
        memoryPanelParent.remove(memoryPanel);
        memoryPanel = null;
    }

    private void removeOldBookmarkPanel() {
        bookmarkPanelParent.remove(bookmarkPanel);
        bookmarkPanel = null;
    }

    private void removeOldDataInspectorPanel() {
        dataInspectorPanelParent.remove(dataInspectorPanel);
        dataInspectorPanel = null;
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
        //Box contents = Box.createVerticalBox();
        JPanel contents = new JPanel(new BorderLayout());

        activeProcessLabel = new JLabel("No process selected");
        activeProcessLabel.setMinimumSize(new Dimension(100, 50));
        activeProcessLabel.setOpaque(true);
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.add(activeProcessLabel);
        contents.add(labelPanel, BorderLayout.NORTH);

        memoryPanelParent = new JPanel(new BorderLayout());
        bookmarkPanelParent = new JPanel(new BorderLayout());

        JSplitPane controlsSplitPane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            memoryPanelParent,
            bookmarkPanelParent
        );
        controlsSplitPane.setDividerLocation(200);
        Dimension minSize = new Dimension(100, 50);
        bookmarkPanelParent.setMinimumSize(minSize);
        memoryPanelParent.setMinimumSize(minSize);
        controlsSplitPane.setResizeWeight(0.8);

        dataInspectorPanelParent = new JPanel(new BorderLayout());
        dataInspectorPanelParent.setMinimumSize(new Dimension(100, 0));
        dataInspectorPanelParent.setPreferredSize(new Dimension(230, 0));
        dataInspectorPanelParent.setMaximumSize(new Dimension(250, 0));

        JSplitPane dataInspectorSplitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            controlsSplitPane,
            dataInspectorPanelParent
        );
        dataInspectorSplitPane.setResizeWeight(0.9);
        dataInspectorSplitPane.setDividerLocation(0.9d);
        dataInspectorSplitPane.setOpaque(true);
        dataInspectorSplitPane.setBackground(Color.magenta);

        contents.add(dataInspectorSplitPane, BorderLayout.CENTER);
        frame.setContentPane(contents);
    }
    //</editor-fold>

    @Override
    public void attach(MainCallbacks mainCallbacks) {
        this.callbacks = mainCallbacks;
    }

    public void refresh() {
        frame.validate();
        frame.repaint();
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
}
