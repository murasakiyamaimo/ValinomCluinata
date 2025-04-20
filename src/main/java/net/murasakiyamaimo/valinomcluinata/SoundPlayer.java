package net.murasakiyamaimo.valinomcluinata;

import javafx.concurrent.Task;

import javax.sound.sampled.LineUnavailableException;
import java.util.ArrayList;

public class SoundPlayer extends Task<Void> {
    private final int synType;
    private final int timeField;
    private final ArrayList<ArrayList<Double>> frequencies;
    public Synthesizer syn = new Synthesizer();
    private volatile boolean running = true;

    public SoundPlayer(int synType, int timeField, ArrayList<ArrayList<Double>> frequencies) {
        this.synType = synType; // pitch配列はコピーして保持
        this.timeField = timeField;
        this.frequencies = frequencies;
    }

    public void stopSound() {
        running = false;
        syn.stopSound();
    }

    @Override
    public Void call() {
        for (ArrayList<Double> frequency : frequencies) {
            double[] frequencies_double = new double[frequency.size()];
            for (int j = 0; j < frequency.size(); j++) {
                frequencies_double[j] = frequency.get(j);
            }
            if (!running) {
                break;
            }
            try {
                syn.playSound(frequencies_double, synType, timeField);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}