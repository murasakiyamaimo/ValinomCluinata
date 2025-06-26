package net.murasakiyamaimo.valinomcluinata;

public class Filter {
    private String type = "Low Pass";
    private double cutoff = 1000;
    private double resonance = 1;

    private double x1 = 0, x2 = 0;
    private double y1 = 0, y2 = 0;

    public void setType(String type) { this.type = type; }
    public void setCutoff(double cutoff) { this.cutoff = cutoff; }
    public void setResonance(double resonance) { this.resonance = resonance; }

    public double process(double input) {
        double frequency = cutoff / 44100.0;
        double omega = 2 * Math.PI * frequency;
        double sin = Math.sin(omega);
        double cos = Math.cos(omega);
        double alpha = sin / (2 * resonance);

        double b0, b1, b2, a0, a1, a2;

        switch (type) {
            case "Low Pass":
                b0 = (1 - cos) / 2;
                b1 = 1 - cos;
                b2 = (1 - cos) / 2;
                a0 = 1 + alpha;
                a1 = -2 * cos;
                a2 = 1 - alpha;
                break;
            case "High Pass":
                b0 = (1 + cos) / 2;
                b1 = -(1 + cos);
                b2 = (1 + cos) / 2;
                a0 = 1 + alpha;
                a1 = -2 * cos;
                a2 = 1 - alpha;
                break;
            default: // Band Pass
                b0 = alpha;
                b1 = 0;
                b2 = -alpha;
                a0 = 1 + alpha;
                a1 = -2 * cos;
                a2 = 1 - alpha;
                break;
        }

        double output = (b0/a0) * input + (b1/a0) * x1 + (b2/a0) * x2
                - (a1/a0) * y1 - (a2/a0) * y2;

        x2 = x1;
        x1 = input;
        y2 = y1;
        y1 = output;

        return output;
    }
}