package cx.corp.lacuna.ui;

import com.google.gson.GsonBuilder;
import cx.corp.lacuna.core.LacunaBootstrap;
import cx.corp.lacuna.core.ProcessOpenException;
import cx.corp.lacuna.ui.model.BookmarkModel;
import cx.corp.lacuna.ui.model.BookmarkPersistence;
import cx.corp.lacuna.ui.model.MainModel;
import cx.corp.lacuna.ui.model.SettingsModel;
import cx.corp.lacuna.ui.presenter.MainPresenter;
import cx.corp.lacuna.ui.view.BookmarkComponent;
import cx.corp.lacuna.ui.view.ChooseProcessDialog;
import cx.corp.lacuna.ui.view.MainWindow;
import cx.corp.lacuna.ui.view.MemoryComponent;

import javax.swing.*;
import java.nio.file.Paths;

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

        BookmarkPersistence bookmarkPersistence = new BookmarkPersistence(
            new GsonBuilder().setPrettyPrinting().create(),
            Paths.get("bookmarks.json")
        );
        BookmarkModel bookmarkModel = new BookmarkModel(bookmarkPersistence);
        BookmarkComponent bookmarkComponent = new BookmarkComponent(
            bookmarkModel,
            settings.getBootstrap().getMemoryReader(),
            settings.getBootstrap().getMemoryWriter()
        );

        MainModel mainModel = new MainModel();
        mainModel.addObserver((o, arg) -> {
            MainModel model = (MainModel) o;
            memoryProvider.setActiveProcess(model.getActiveProcess());
            memoryComponent.notifyProviderUpdated();
        });
        MainWindow mainWindow = new MainWindow(new ChooseProcessDialog(settings));
        mainWindow.setMemoryPanel(memoryComponent.getPanel());
        mainWindow.setBookmarkPanel(bookmarkComponent.getPanel());
        MainPresenter presenter = new MainPresenter(mainWindow, mainModel);

        memoryProvider.setMemoryAccessExceptionHandler(ProcessOpenException.class, ex -> {
            // ¯\_(ツ)_/¯'
            mainWindow.setActiveProcess(null);
            presenter.newActiveProcessSelected();
            mainWindow.refresh(); // validate & repaint so that the active process label updates
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
