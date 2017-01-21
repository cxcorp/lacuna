package cx.corp.lacuna.ui;

import com.sun.jna.Native;
import cx.corp.lacuna.core.NativeProcess;
import cx.corp.lacuna.core.NativeProcessEnumerator;
import cx.corp.lacuna.core.windows.WindowsNativeProcessEnumerator;
import cx.corp.lacuna.core.windows.winapi.Kernel32;
import cx.corp.lacuna.core.windows.winapi.Psapi;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        Kernel32 kernel = Native.loadLibrary("Kernel32", Kernel32.class);
        Psapi psapi = Native.loadLibrary("Psapi", Psapi.class);

        NativeProcessEnumerator enumerator = new WindowsNativeProcessEnumerator(kernel, psapi);
        List<NativeProcess> processes = enumerator.getProcesses();

        for (NativeProcess proc : processes) {
            System.out.printf("%-5d %s%n", proc.getPid(), proc.getDescription());
        }

        //launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Main.fxml"));
        Scene scene = new Scene(root, 300, 275);
        stage.setScene(scene);
        stage.setTitle("Lacuna");
        stage.show();
    }
}