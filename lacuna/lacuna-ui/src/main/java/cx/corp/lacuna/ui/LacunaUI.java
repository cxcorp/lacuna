package cx.corp.lacuna.ui;

import cx.corp.lacuna.ui.model.MainModel;
import cx.corp.lacuna.ui.model.MainModelImpl;
import cx.corp.lacuna.ui.presenter.MainPresenter;
import cx.corp.lacuna.ui.view.MainWindow;

import javax.swing.*;

public class LacunaUI implements Runnable {
    public static void main(String... args) {
        SwingUtilities.invokeLater(new LacunaUI());
    }

    @Override
    public void run() {
        MainModel mainModel = new MainModelImpl();
        MainWindow mainWindow = new MainWindow();
        MainPresenter presenter = new MainPresenter(mainWindow, mainModel);
        presenter.initialize();
        mainWindow.show();
    }
}
