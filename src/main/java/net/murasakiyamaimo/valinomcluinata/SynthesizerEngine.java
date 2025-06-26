package net.murasakiyamaimo.valinomcluinata;

import javax.sound.sampled.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SynthesizerEngine {
    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = 1024;

    private SourceDataLine audioLine;
    private Oscillator[] oscillators = new Oscillator[3];
    private ADSREnvelope envelope = new ADSREnvelope();
    private Filter filter = new Filter();

    private Set<Integer> pressedKeys = ConcurrentHashMap.newKeySet();
    private double masterVolume = 0.5;

    // 波形データ（GUI用）
    private double[] waveformData = new double[800];
    private int waveformIndex = 0;

    public SynthesizerEngine() {
        initializeAudio();
        initializeOscillators();
        startAudioThread();
    }

    private void initializeAudio() {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            audioLine = (SourceDataLine) AudioSystem.getLine(info);
            audioLine.open(format, BUFFER_SIZE * 4);
            audioLine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeOscillators() {
        for (int i = 0; i < 3; i++) {
            oscillators[i] = new Oscillator();
        }
    }

    private void startAudioThread() {
        Thread audioThread = new Thread(() -> {
            byte[] buffer = new byte[BUFFER_SIZE * 2];

            while (true) {
                generateAudio(buffer);
                audioLine.write(buffer, 0, buffer.length);
            }
        });
        audioThread.setDaemon(true);
        audioThread.start();
    }

    private void generateAudio(byte[] buffer) {
        for (int i = 0; i < BUFFER_SIZE; i++) {
            double sample = 0;

            for (Integer note : pressedKeys) {
                double frequency = 440.0 * Math.pow(2.0, (note - 69) / 12.0);

                for (Oscillator osc : oscillators) {
                    sample += osc.getSample(frequency);
                }
            }

            sample *= envelope.getAmplitude();
            sample = filter.process(sample);
            sample *= masterVolume;
            sample = Math.max(-1.0, Math.min(1.0, sample));

            waveformData[waveformIndex] = sample;
            waveformIndex = (waveformIndex + 1) % waveformData.length;

            short shortSample = (short) (sample * 32767);
            buffer[i * 2] = (byte) (shortSample & 0xFF);
            buffer[i * 2 + 1] = (byte) ((shortSample >> 8) & 0xFF);
        }
    }

    // パブリックAPI
    public void noteOn(int note) {
        if (!pressedKeys.contains(note)) {
            pressedKeys.add(note);
            envelope.noteOn();
        }
    }

    public void noteOff(int note) {
        if (pressedKeys.contains(note)) {
            pressedKeys.remove(note);
            if (pressedKeys.isEmpty()) {
                envelope.noteOff();
            }
        }
    }

    public void setMasterVolume(double volume) {
        this.masterVolume = volume;
    }

    public Oscillator getOscillator(int index) {
        return oscillators[index];
    }

    public ADSREnvelope getEnvelope() {
        return envelope;
    }

    public Filter getFilter() {
        return filter;
    }

    public double[] getWaveformData() {
        return waveformData.clone();
    }

    public void shutdown() {
        if (audioLine != null) {
            audioLine.close();
        }
    }
}
