package cx.corp.lacuna.ui;

import com.sun.jna.Native;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.windows.WindowsNativeProcessEnumerator;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.Psapi;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ex) {
            System.err.println("Failed to set look and feel: " + ex);
        }

        MainWindow mainWindow = new MainWindow();
        SwingUtilities.invokeLater(mainWindow::show);
    }
}