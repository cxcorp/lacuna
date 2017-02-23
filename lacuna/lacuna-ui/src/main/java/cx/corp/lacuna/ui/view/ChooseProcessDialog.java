package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.ui.model.ProcessListModel;
import cx.corp.lacuna.ui.model.SettingsModel;
import cx.corp.lacuna.ui.presenter.ProcessChosenEventListener;
import cx.corp.lacuna.ui.presenter.ProcessListPresenter;

import java.awt.*;

public class ChooseProcessDialog {

    private final SettingsModel settings;

    public ChooseProcessDialog(SettingsModel settings) {
        this.settings = settings;
    }

    public void showDialogWithCallback(Window parent, ProcessChosenEventListener callback) {
        ProcessListModel m = new ProcessListModel(settings);
        ProcessListWindow w = new ProcessListWindow(parent);
        ProcessListPresenter p = new ProcessListPresenter(w, m);
        p.addProcessChosenListener(callback);
        p.initialize();
        w.show();
    }
}