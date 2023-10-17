package mx.com.sigrama.ars.device;

public class OscilloscopeData {
    private class point {
        double t; //t is time
        double[] y;
    }
    private point[] points;
    public OscilloscopeData (point[] points) {
        this.points = points;
    }


}
