package net.murasakiyamaimo.valinomcluinata;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javax.sound.sampled.LineUnavailableException;

public class SynthesizerController {
    @FXML
    private Button Button;
    @FXML
    private TextField TextField;
    @FXML
    private ComboBox ComboBox;

    private int type = 0;

    public void initialize() {
        TextField.setText("261.63, 392.44, 327.03, 490.55");
        ComboBox.setValue("SINE");
        ComboBox.getItems().addAll("SINE", "SAWTOOTH", "SQUARE", "TRIANGLE");

        ComboBox.setOnAction(event -> {
            if (ComboBox.getValue() == "SINE") {
                type = Synthesizer.SINE;
            } else if (ComboBox.getValue() == "SAWTOOTH") {
                type = Synthesizer.SAWTOOTH;
            } else if (ComboBox.getValue() == "SQUARE") {
                type = Synthesizer.SQUARE;
            } else if (ComboBox.getValue() == "TRIANGLE") {
                type = Synthesizer.TRIANGLE;
            }
        });

        Button.setOnAction(event -> {
            String input = TextField.getText().trim();
            String[] stringFrequencies = input.split(",");
            Double[] stringToDouble = new Double[stringFrequencies.length];
            double[] frequency = new double[stringToDouble.length];

            for (int i = 0; i < stringFrequencies.length; i++) {
                stringToDouble[i] = Double.parseDouble(stringFrequencies[i]);
            }

            for (int i = 0; i < stringToDouble.length; i++) {
                frequency[i] = stringToDouble[i];
                System.out.println(frequency[i]);// オートボクシング解除
            }




            Synthesizer syn = new Synthesizer();
            new Thread(() -> {
                try {
                    syn.playSound(frequency, type, 1);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        });
    }
}

