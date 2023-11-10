package mx.com.sigrama.ars.common;


/*
    * Created by SKGadi on 8th November 2023
    * Allows to interpolate data using linear interpolation
    * Takes input data in x and y
    * Generates a linear interpolation for y = f(x)
 */
public class LinearInterpolator {
    float [] x;
    float [] y;
    public LinearInterpolator(float [] x, float [] y) {
        this.x = x;
        this.y = y;
    }
    public float interpolate(float x) {
        // Handle the boundary cases.
        final int n = this.x.length;
        if (Float.isNaN(x)) {
            return x;
        }
        if (x <= this.x[0]) {
            return this.y[0];
        }
        if (x >= this.x[n - 1]) {
            return this.y[n - 1];
        }

        // Find the index 'i' of the last point with smaller X.
        // We know this will be within the spline due to the boundary tests.
        int i = 0;
        while (x >= this.x[i + 1]) {
            i += 1;
            if (x == this.x[i]) {
                return this.y[i];
            }
        }

        // Perform linear interpolation.
        float h = this.x[i + 1] - this.x[i];
        float t = (x - this.x[i]) / h;
        return (this.y[i] * (1 - t) + this.y[i + 1] * t);
    }
    // For debugging.
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        final int n = this.x.length;
        str.append("[");
        for (int i = 0; i < n; i++) {
            if (i != 0) {
                str.append(", ");
            }
            str.append("(").append(this.x[i]);
            str.append(", ").append(this.y[i]);
            str.append(")");
        }
        str.append("]");
        return str.toString();
    }
}
