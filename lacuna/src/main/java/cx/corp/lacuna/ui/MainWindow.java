package cx.corp.lacuna.ui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

public class MainWindow {

    private JFrame frame;

    public MainWindow() {
        createFrame();
    }

    public void show() {
        frame.setVisible(true);
    }

    private void createFrame() {
        frame = new JFrame("Lacuna");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        createMenuBar();
        createComponents();

        frame.setPreferredSize(new Dimension(600, 400));
        frame.pack();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem menuItem = new JMenuItem("Choose process...");
        fileMenu.add(menuItem);


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
}
