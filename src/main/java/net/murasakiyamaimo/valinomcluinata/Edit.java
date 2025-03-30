package net.murasakiyamaimo.valinomcluinata;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Edit extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Edit.class.getResource("Editor" + ".fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        EditorController controller = fxmlLoader.getController();
        Synthesizer syn = new Synthesizer();
        double[] frequency = {261.63, 392.44, 327.03, 490.55};
        stage.setTitle("ValinomCluinata");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();

    }
}
