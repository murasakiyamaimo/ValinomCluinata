package net.murasakiyamaimo.valinomcluinata;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Edit extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Edit.class.getResource("Editor" + ".fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        EditorController controller = fxmlLoader.getController();
        stage.setTitle("ValinomCluinata");
        stage.getIcons().add(new Image(Objects.requireNonNull(Edit.class.getResourceAsStream("ValinomCluinata_icon.png"))));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();

    }
}
