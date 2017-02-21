package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.core.LacunaBootstrap;
import cx.corp.lacuna.ui.model.ProcessListModel;
import cx.corp.lacuna.ui.presenter.ProcessChosenEventListener;
import cx.corp.lacuna.ui.presenter.ProcessListPresenter;

import java.awt.*;

class ChooseProcessDialog {

    static void showDialogWithCallback(Window parent, ProcessChosenEventListener callback) {
        ProcessListModel m = LacunaBootstrap.forCurrentPlatform().getNativeProcessEnumerator()::getProcesses;
        ProcessListWindow w = new ProcessListWindow(parent);
        ProcessListPresenter p = new ProcessListPresenter(w, m);
        p.addProcessChosenListener(callback);
        p.initialize();
        w.show();
    }
}