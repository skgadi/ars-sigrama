package mx.com.sigrama.ars.common;

import android.content.Context;
import android.util.Log;
import android.view.View;

/**
 * Created by SKGadi on 7th November 2023
 * Allows to generate view of a phasor diagram
 * Takes input data such as number of phases, voltage, current
 * Generates a phasor diagram
 */
public class PhasorDiagram extends View {
    int numberOfPhases;
    double [] voltages;
    double [] currents;
    double [] voltageAngles;
    double [] currentAngles;
    public PhasorDiagram(Context context, int numberOfPhases, double [] voltages, double [] currents, double [] voltageAngles, double [] currentAngles) {
        super(context);
        this.numberOfPhases = numberOfPhases;
        this.voltages = voltages;
        this.currents = currents;
        this.voltageAngles = voltageAngles;
        this.currentAngles = currentAngles;
    }

    public PhasorDiagram(Context context) {
        super(context);
    }
    public PhasorDiagram(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);
        //Make background white
        canvas.drawColor(android.graphics.Color.WHITE);
        //Make a paint object
        android.graphics.Paint paint = new android.graphics.Paint();
        //Make the lines red
        paint.setColor(android.graphics.Color.RED);

        //Draw a line on the canvas
        canvas.drawLine(-200, -200, 200, 200, paint);
        //Print time
        paint.setColor(android.graphics.Color.BLACK);
        paint.setTextSize(30);
        canvas.drawText("Time: " + System.currentTimeMillis(), 200, 200, paint);

    }


}
