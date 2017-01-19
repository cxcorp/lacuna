package cx.corp.lacuna.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class MainController {
    @FXML private Text actiontarget;

    @FXML protected void handleTestButtonAction(ActionEvent event) {
        actiontarget.setText("Hello World!");
    }
}
