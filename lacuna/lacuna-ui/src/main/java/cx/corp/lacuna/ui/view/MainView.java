package cx.corp.lacuna.ui.view;

import cx.corp.lacuna.ui.presenter.MainCallbacks;

public interface MainView extends View<MainCallbacks> {

    void setActiveProcess(int newPid);
}
