import com.edp2021c1.randomseatgenerator.ui.window.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Test {
    public static void main(String[] args) {
        Application.launch(App.class, args);
    }

    public static class App extends Application {
        @Override
        public void start(Stage primaryStage) {
            new MainWindow().show();
        }
    }
}
