package net.murasakiyamaimo.valinomcluinata;

public class Oscillator {
    private String waveform = "Sine";
    private double volume = 0.3;
    private double detune = 0;
    private double phase = 0;

    public void setWaveform(String waveform) {
        this.waveform = waveform;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void setDetune(double detune) {
        this.detune = detune;
    }

    public double getSample(double frequency) {
        double adjustedFreq = frequency * Math.pow(2, detune / 12.0);
        double increment = 2 * Math.PI * adjustedFreq / 44100;
        phase += increment;

        if (phase > 2 * Math.PI) {
            phase -= 2 * Math.PI;
        }

        double sample = switch (waveform) {
            case "Sine" -> Math.sin(phase);
            case "Square" -> phase < Math.PI ? 1 : -1;
            case "Sawtooth" -> 2 * (phase / (2 * Math.PI)) - 1;
            case "Triangle" -> phase < Math.PI ?
                    4 * phase / Math.PI - 1 :
                    3 - 4 * phase / Math.PI;
            default -> 0;
        };

        return sample * volume;
    }
}