package net.murasakiyamaimo.valinomcluinata;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class VisualSynthesizer extends Application {

    private SynthesizerEngine synthEngine;
    private Canvas waveformCanvas;
    private GraphicsContext gc;

    private Map<KeyCode, Integer> keyToNote = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        synthEngine = new SynthesizerEngine();
        initializeKeyMap();

        VBox root = createMainLayout();

        Scene scene = new Scene(root, 900, 700);
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);

        primaryStage.setTitle("Visual Synthesizer");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> synthEngine.shutdown());
        primaryStage.show();

        root.setFocusTraversable(true);
        root.requestFocus();

        startWaveformAnimation();
    }

    private VBox createMainLayout() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #2b2b2b;");

        Label titleLabel = new Label("Visual Synthesizer");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        titleLabel.setAlignment(Pos.CENTER);

        waveformCanvas = new Canvas(800, 200);
        waveformCanvas.setStyle("-fx-border-color: #555555; -fx-border-width: 2px;");
        gc = waveformCanvas.getGraphicsContext2D();

        HBox oscillatorSection = createOscillatorSection();
        VBox adsrSection = createADSRSection();
        VBox filterSection = createFilterSection();
        HBox masterSection = createMasterSection();

        Label keyboardHint = new Label("キーボード: A-K でC3-C4 (黒鍵: W,E,T,Y,U,O,P)");
        keyboardHint.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");

        root.getChildren().addAll(
                titleLabel,
                waveformCanvas,
                oscillatorSection,
                adsrSection,
                filterSection,
                masterSection,
                keyboardHint
        );

        return root;
    }

    private HBox createOscillatorSection() {
        HBox section = new HBox(20);
        section.setAlignment(Pos.CENTER);

        for (int i = 0; i < 3; i++) {
            VBox oscBox = createOscillatorControls(i);
            section.getChildren().add(oscBox);
        }

        return section;
    }

    private VBox createOscillatorControls(int index) {
        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #666666; -fx-border-width: 1px; -fx-padding: 10px; -fx-background-color: #3a3a3a;");

        Label title = new Label("Oscillator " + (index + 1));
        title.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");

        ComboBox<String> waveformBox = new ComboBox<>();
        waveformBox.getItems().addAll("Sine", "Square", "Sawtooth", "Triangle");
        waveformBox.setValue("Sine");
        waveformBox.setOnAction(e -> synthEngine.getOscillator(index).setWaveform(waveformBox.getValue()));

        Slider volumeSlider = new Slider(0, 1, 0.3);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.valueProperty().addListener((obs, old, val) ->
                synthEngine.getOscillator(index).setVolume(val.doubleValue()));

        Slider detuneSlider = new Slider(-12, 12, 0);
        detuneSlider.setShowTickLabels(true);
        detuneSlider.setShowTickMarks(true);
        detuneSlider.valueProperty().addListener((obs, old, val) ->
                synthEngine.getOscillator(index).setDetune(val.doubleValue()));

        box.getChildren().addAll(
                title,
                new Label("Waveform:") {{ setStyle("-fx-text-fill: #cccccc;"); }},
                waveformBox,
                new Label("Volume:") {{ setStyle("-fx-text-fill: #cccccc;"); }},
                volumeSlider,
                new Label("Detune:") {{ setStyle("-fx-text-fill: #cccccc;"); }},
                detuneSlider
        );

        return box;
    }

    private VBox createADSRSection() {
        VBox section = new VBox(5);
        section.setStyle("-fx-border-color: #666666; -fx-border-width: 1px; -fx-padding: 10px; -fx-background-color: #3a3a3a;");

        Label title = new Label("ADSR Envelope");
        title.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");

        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);

        VBox attackBox = new VBox(5);
        Slider attackSlider = new Slider(0.01, 3.0, 0.1);
        attackSlider.setOrientation(javafx.geometry.Orientation.VERTICAL);
        attackSlider.setPrefHeight(100);
        attackSlider.valueProperty().addListener((obs, old, val) ->
                synthEngine.getEnvelope().setAttack(val.doubleValue()));
        attackBox.getChildren().addAll(
                new Label("Attack") {{ setStyle("-fx-text-fill: #cccccc;"); }},
                attackSlider
        );

        VBox decayBox = new VBox(5);
        Slider decaySlider = new Slider(0.01, 3.0, 0.3);
        decaySlider.setOrientation(javafx.geometry.Orientation.VERTICAL);
        decaySlider.setPrefHeight(100);
        decaySlider.valueProperty().addListener((obs, old, val) ->
                synthEngine.getEnvelope().setDecay(val.doubleValue()));
        decayBox.getChildren().addAll(
                new Label("Decay") {{ setStyle("-fx-text-fill: #cccccc;"); }},
                decaySlider
        );

        VBox sustainBox = new VBox(5);
        Slider sustainSlider = new Slider(0, 1, 0.7);
        sustainSlider.setOrientation(javafx.geometry.Orientation.VERTICAL);
        sustainSlider.setPrefHeight(100);
        sustainSlider.valueProperty().addListener((obs, old, val) ->
                synthEngine.getEnvelope().setSustain(val.doubleValue()));
        sustainBox.getChildren().addAll(
                new Label("Sustain") {{ setStyle("-fx-text-fill: #cccccc;"); }},
                sustainSlider
        );

        VBox releaseBox = new VBox(5);
        Slider releaseSlider = new Slider(0.01, 3.0, 0.5);
        releaseSlider.setOrientation(javafx.geometry.Orientation.VERTICAL);
        releaseSlider.setPrefHeight(100);
        releaseSlider.valueProperty().addListener((obs, old, val) ->
                synthEngine.getEnvelope().setRelease(val.doubleValue()));
        releaseBox.getChildren().addAll(
                new Label("Release") {{ setStyle("-fx-text-fill: #cccccc;"); }},
                releaseSlider
        );

        controls.getChildren().addAll(attackBox, decayBox, sustainBox, releaseBox);
        section.getChildren().addAll(title, controls);

        return section;
    }

    private VBox createFilterSection() {
        VBox section = new VBox(5);
        section.setStyle("-fx-border-color: #666666; -fx-border-width: 1px; -fx-padding: 10px; -fx-background-color: #3a3a3a;");

        Label title = new Label("Filter");
        title.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");

        HBox controls = new HBox(20);
        controls.setAlignment(Pos.CENTER);

        ComboBox<String> filterTypeBox = new ComboBox<>();
        filterTypeBox.getItems().addAll("Low Pass", "High Pass", "Band Pass");
        filterTypeBox.setValue("Low Pass");
        filterTypeBox.setOnAction(e -> synthEngine.getFilter().setType(filterTypeBox.getValue()));

        Slider cutoffSlider = new Slider(50, 20000, 1000);
        cutoffSlider.setShowTickLabels(true);
        cutoffSlider.setShowTickMarks(true);
        cutoffSlider.valueProperty().addListener((obs, old, val) ->
                synthEngine.getFilter().setCutoff(val.doubleValue()));

        Slider resonanceSlider = new Slider(0.1, 10, 1);
        resonanceSlider.setShowTickLabels(true);
        resonanceSlider.setShowTickMarks(true);
        resonanceSlider.valueProperty().addListener((obs, old, val) ->
                synthEngine.getFilter().setResonance(val.doubleValue()));

        VBox typeBox = new VBox(5);
        typeBox.getChildren().addAll(
                new Label("Type:") {{ setStyle("-fx-text-fill: #cccccc;"); }},
                filterTypeBox
        );

        VBox cutoffBox = new VBox(5);
        cutoffBox.getChildren().addAll(
                new Label("Cutoff:") {{ setStyle("-fx-text-fill: #cccccc;"); }},
                cutoffSlider
        );

        VBox resonanceBox = new VBox(5);
        resonanceBox.getChildren().addAll(
                new Label("Resonance:") {{ setStyle("-fx-text-fill: #cccccc;"); }},
                resonanceSlider
        );

        controls.getChildren().addAll(typeBox, cutoffBox, resonanceBox);
        section.getChildren().addAll(title, controls);

        return section;
    }

    private HBox createMasterSection() {
        HBox section = new HBox(20);
        section.setAlignment(Pos.CENTER);
        section.setStyle("-fx-border-color: #666666; -fx-border-width: 1px; -fx-padding: 10px; -fx-background-color: #3a3a3a;");

        Label title = new Label("Master Volume");
        title.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");

        Slider masterVolumeSlider = new Slider(0, 1, 0.5);
        masterVolumeSlider.setShowTickLabels(true);
        masterVolumeSlider.setShowTickMarks(true);
        masterVolumeSlider.setPrefWidth(200);
        masterVolumeSlider.valueProperty().addListener((obs, old, val) ->
                synthEngine.setMasterVolume(val.doubleValue()));

        section.getChildren().addAll(title, masterVolumeSlider);

        return section;
    }

    private void initializeKeyMap() {
        keyToNote.put(KeyCode.A, 60); // C4
        keyToNote.put(KeyCode.S, 62); // D4
        keyToNote.put(KeyCode.D, 64); // E4
        keyToNote.put(KeyCode.F, 65); // F4
        keyToNote.put(KeyCode.G, 67); // G4
        keyToNote.put(KeyCode.H, 69); // A4
        keyToNote.put(KeyCode.J, 71); // B4
        keyToNote.put(KeyCode.K, 72); // C5

        keyToNote.put(KeyCode.W, 61); // C#4
        keyToNote.put(KeyCode.E, 63); // D#4
        keyToNote.put(KeyCode.T, 66); // F#4
        keyToNote.put(KeyCode.Y, 68); // G#4
        keyToNote.put(KeyCode.U, 70); // A#4
        keyToNote.put(KeyCode.O, 73); // C#5
        keyToNote.put(KeyCode.P, 75); // D#5
    }

    private void handleKeyPressed(KeyEvent event) {
        Integer note = keyToNote.get(event.getCode());
        if (note != null) {
            synthEngine.noteOn(note);
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        Integer note = keyToNote.get(event.getCode());
        if (note != null) {
            synthEngine.noteOff(note);
        }
    }

    private void startWaveformAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawWaveform();
            }
        };
        timer.start();
    }

    private void drawWaveform() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, waveformCanvas.getWidth(), waveformCanvas.getHeight());

        gc.setStroke(Color.LIME);
        gc.setLineWidth(2);

        double centerY = waveformCanvas.getHeight() / 2;
        double amplitude = centerY * 0.8;

        double[] waveformData = synthEngine.getWaveformData();

        gc.beginPath();
        for (int i = 0; i < waveformData.length - 1; i++) {
            double x1 = i * waveformCanvas.getWidth() / waveformData.length;
            double y1 = centerY - waveformData[i] * amplitude;
            double x2 = (i + 1) * waveformCanvas.getWidth() / waveformData.length;
            double y2 = centerY - waveformData[i + 1] * amplitude;

            if (i == 0) {
                gc.moveTo(x1, y1);
            } else {
                gc.lineTo(x2, y2);
            }
        }
        gc.stroke();
    }

    public static void main(String[] args) {
        launch(args);
    }
}