package cx.corp.lacuna.ui.model;

import cx.corp.lacuna.core.LacunaBootstrap;

public class SettingsModel {

    private LacunaBootstrap bootstrap;

    public SettingsModel() {
        this(null);
    }

    public SettingsModel(LacunaBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public LacunaBootstrap getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(LacunaBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }
}
