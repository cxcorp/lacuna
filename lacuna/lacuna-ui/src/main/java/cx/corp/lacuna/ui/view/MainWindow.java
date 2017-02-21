package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.domain.NativeProcess;
import cx.corp.lacuna.ui.presenter.MainCallbacks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

public class MainWindow implements MainView {

    private JFrame frame;
    private MainCallbacks callbacks;

    public MainWindow() {
        createWindow();
    }

    public void show() {
        frame.setVisible(true);
    }

    private void createWindow() {
        createFrame();
        createMenuBar();
        createComponents();
        frame.pack();
    }

    private void createFrame() {
        frame = new JFrame("Lacuna");
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
            ChooseProcessDialog.showDialogWithCallback(
                this.frame,
                callbacks::newActiveProcessSelected
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
        contents.setOpaque(true);

        JTextArea txt = new JTextArea(5, 30);
        contents.add(txt, BorderLayout.CENTER);

        frame.setContentPane(contents);
    }

    @Override
    public void attach(MainCallbacks mainCallbacks) {
        this.callbacks = mainCallbacks;
    }

    @Override
    public void setActiveProcess(NativeProcess newProcess) {
    }
}
