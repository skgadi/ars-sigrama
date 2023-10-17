package mx.com.sigrama.ars.device;

public class HarmonicsData {
    public class harmonic {
        double frequency;
        double amplitude;
        double phase;
    }
    private harmonic[] harmonics;
    public HarmonicsData (harmonic[] harmonics) {
        this.harmonics = harmonics;
    }
}
