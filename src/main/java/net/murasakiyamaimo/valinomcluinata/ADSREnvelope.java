package net.murasakiyamaimo.valinomcluinata;

public class ADSREnvelope {
    private double attack = 0.1;
    private double decay = 0.3;
    private double sustain = 0.7;
    private double release = 0.5;

    private double amplitude = 0;
    private boolean notePressed = false;
    private double phase = 0;

    public void setAttack(double attack) { this.attack = attack; }
    public void setDecay(double decay) { this.decay = decay; }
    public void setSustain(double sustain) { this.sustain = sustain; }
    public void setRelease(double release) { this.release = release; }

    public void noteOn() {
        notePressed = true;
        phase = 0;
    }

    public void noteOff() {
        notePressed = false;
    }

    public double getAmplitude() {
        double dt = 1.0 / 44100.0;

        if (notePressed) {
            if (phase < attack) {
                amplitude = phase / attack;
            } else if (phase < attack + decay) {
                double decayPhase = (phase - attack) / decay;
                amplitude = 1.0 - (1.0 - sustain) * decayPhase;
            } else {
                amplitude = sustain;
            }
            phase += dt;
        } else {
            amplitude *= Math.pow(0.001, dt / release);
        }

        return amplitude;
    }
}