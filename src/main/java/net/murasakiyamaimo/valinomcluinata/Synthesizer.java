package net.murasakiyamaimo.valinomcluinata;

import javax.sound.sampled.*;

public class Synthesizer {
    public static final int SINE = 0;
    public static final int SAWTOOTH = 1;
    public static final int SQUARE = 2;
    public static final int TRIANGLE = 3;
    private final int SAMPLE_RATE = 44100;
    private boolean isPlaying = false;
    private SourceDataLine sourceLine;

    public void playSound(double[] frequencies, int type, int DURATION) throws LineUnavailableException {

        // AudioFormatの設定
        AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);

        // SourceDataLineの取得
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceLine.open(audioFormat);
        sourceLine.start();
        isPlaying = true;

        // サイン波データの生成
        byte[] buffer = generateWaves(frequencies, type, DURATION);

        // 再生
        sourceLine.write(buffer, 0, buffer.length);

        // 終了処理
        sourceLine.drain();
        sourceLine.close();
        isPlaying = false;

        System.out.println(DURATION + " 秒間再生しました。");
        for (double frequency : frequencies) {
            System.out.println(frequency + " Hz");
        }
    }

    public void stopSound() {
        if (sourceLine != null && isPlaying) {
            sourceLine.stop();
            sourceLine.flush();
            isPlaying = false;
        }
    }

    private byte[] generateWaves(double[] frequencies, int type, int DURATION) {
        int numSamples = SAMPLE_RATE * DURATION;
        byte[] buffer = new byte[numSamples * 2];
        double[] combinedValues = new double[numSamples];

        for (double frequency : frequencies) {
            for (int i = 0; i < numSamples; i++) {
                double time = (double) i / SAMPLE_RATE;

                if (type == SINE) {
                    combinedValues[i] += generateSineValue(frequency, time);
                } else if (type == SAWTOOTH) {
                    combinedValues[i] += generateSawtoothValue(frequency, time);
                } else if (type == SQUARE) {
                    combinedValues[i] += generateSquareValue(frequency, time);
                } else if (type == TRIANGLE) {
                    combinedValues[i] += generateTriangleValue(frequency, time);
                }
            }
        }

        // 音量が大きくなりすぎないように調整
        double normalizationFactor = 1.0 / (frequencies.length * 4); // 重ねる波形の種類の数で割る
        for (int i = 0; i < numSamples; i++) {
            combinedValues[i] *= normalizationFactor;
        }

        for (int i = 0; i < numSamples; i++) {
            short amplitude = (short) (combinedValues[i] * 32767);
            buffer[i * 2] = (byte) (amplitude & 0xFF);
            buffer[i * 2 + 1] = (byte) ((amplitude >> 8) & 0xFF);
        }

        return buffer;
    }

    // サイン波の値を計算する関数 (時間 t を引数に追加)
    private static double generateSineValue(double frequency, double time) {
        return Math.sin(2 * Math.PI * frequency * time);
    }

    // ノコギリ波の値を計算する関数 (時間 t を引数に追加)
    private static double generateSawtoothValue(double frequency, double time) {
        double period = 1.0 / frequency;
        double timeInPeriod = time % period;
        return 2.0 * (timeInPeriod / period) - 1.0;
    }

    // 矩形波の値を計算する関数 (時間 t を引数に追加)
    private static double generateSquareValue(double frequency, double time) {
        double period = 1.0 / frequency;
        double halfPeriod = period / 2.0;
        double timeInPeriod = time % period;
        return (timeInPeriod < halfPeriod) ? 1.0 : -1.0;
    }

    // 三角波の値を計算する関数 (時間 t を引数に追加)
    private static double generateTriangleValue(double frequency, double time) {
        double period = 1.0 / frequency;
        double quarterPeriod = period / 4.0;
        double timeInPeriod = time % period;

        if (timeInPeriod < quarterPeriod) {
            return 4.0 * timeInPeriod / period;
        } else if (timeInPeriod < 3 * quarterPeriod) {
            return 1.0 - 4.0 * (timeInPeriod - quarterPeriod) / period;
        } else {
            return -1.0 + 4.0 * (timeInPeriod - 3 * quarterPeriod) / period;
        }
    }
}
