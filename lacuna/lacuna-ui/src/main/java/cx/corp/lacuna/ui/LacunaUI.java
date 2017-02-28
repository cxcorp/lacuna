package cx.corp.lacuna.ui;

import cx.corp.lacuna.core.LacunaBootstrap;
import cx.corp.lacuna.core.MemoryAccessException;
import cx.corp.lacuna.core.ProcessOpenException;
import cx.corp.lacuna.ui.model.MainModel;
import cx.corp.lacuna.ui.model.SettingsModel;
import cx.corp.lacuna.ui.presenter.MainPresenter;
import cx.corp.lacuna.ui.view.ChooseProcessDialog;
import cx.corp.lacuna.ui.view.MainWindow;
import cx.corp.lacuna.ui.view.MemoryComponent;
import org.exbin.utils.binary_data.EditableBinaryData;

import javax.swing.*;

public class LacunaUI implements Runnable {
    public static void main(String... args) {
        SwingUtilities.invokeLater(new LacunaUI());
    }

    @Override
    public void run() {
        SettingsModel settings = new SettingsModel();
        settings.setBootstrap(LacunaBootstrap.forCurrentPlatform());

        ProcessBinaryData memoryProvider = new ProcessBinaryData(
            settings.getBootstrap().getMemoryReader(),
            settings.getBootstrap().getMemoryWriter()
        );
        MemoryComponent memoryComponent = new MemoryComponent(memoryProvider);

        MainModel mainModel = new MainModel();
        mainModel.addObserver((o, arg) -> {
            MainModel model = (MainModel) o;
            memoryProvider.setActiveProcess(model.getActiveProcess());
        });
        MainWindow mainWindow = new MainWindow(new ChooseProcessDialog(settings));
        mainWindow.setMemoryPanel(memoryComponent.getPanel());
        MainPresenter presenter = new MainPresenter(mainWindow, mainModel);

        memoryProvider.setMemoryAccessExceptionHandler(ProcessOpenException.class, ex -> {
            mainWindow.setActiveProcess(null);
            presenter.newActiveProcessSelected(); // sorry no time ¯\_(ツ)_/¯'
            mainWindow.refresh();
            JOptionPane.showMessageDialog(
                memoryComponent.getPanel(),
                "An error occurred while opening the process!\n" +
                    "Please check that the target process is still running and " +
                    "that you have necessary privileges!",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        });

        presenter.initialize();
        mainWindow.show();
    }
}
