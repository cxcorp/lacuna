package cx.corp.lacuna.ui;

import cx.corp.lacuna.core.LacunaBootstrap;
import cx.corp.lacuna.ui.view.MainWindow;
import cx.corp.lacuna.ui.view.ProcessListWindow;

import javax.swing.*;

public class LacunaUI implements Runnable {
    public static void main(String... args) {
        SwingUtilities.invokeLater(new LacunaUI());
    }

    @Override
    public void run() {
        ProcessListWindow w = new ProcessListWindow();
        w.setProcessList(
            LacunaBootstrap
            .forCurrentPlatform()
            .getNativeProcessEnumerator()
            .getProcesses()
        );
        w.show();
    }
}
