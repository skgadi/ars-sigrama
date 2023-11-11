package mx.com.sigrama.ars.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.components.Legend;

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
    double fontSizeDpGridText = 10; // font size in dp for grid text
    double strokeSizeDpVoltageArrow = 7; // stroke size in dp for voltage arrow
    double strokeSizeDpCurrentArrow = 5; // stroke size in dp for current arrow

    /**
     * This enum shows type of the arrow
     */
    enum ArrowType {
        VOLTAGE,
        CURRENT
    }

    /**
     * Class to hold the canvas stats
     */
    class CanvasStats {
        int width;
        int height;
        int centerX;
        int centerY;
        CanvasStats(@NonNull android.graphics.Canvas canvas) {
            width = canvas.getWidth();
            height = canvas.getHeight();
            centerX = width/2;
            centerY = height/2;
        }
    }
    private CanvasStats canvasStats;
    private long startTime = System.currentTimeMillis();
    private int framesPerSecond = 30;
    private long animationDuration = 10000;
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
        this.canvasStats = new CanvasStats(canvas);

        //calculate time since start for animation
        long timeSinceStart = System.currentTimeMillis() - startTime;


        //Make background white
        canvas.drawColor(Color.WHITE);
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

        // Draw circles for showing polar grid
        drawPolarGrid(canvas);

        // Temporary code to test the arrow
        placeArrowOnCanvas(canvas, 1f, (timeSinceStart/100f)%360 , android.graphics.Color.RED, ArrowType.VOLTAGE);
        placeArrowOnCanvas(canvas, 1f, 60, Color.BLUE, ArrowType.CURRENT);
        if (timeSinceStart < animationDuration) {
            this.postInvalidateDelayed( 1000 / framesPerSecond);
        }

    }

    /**
     * This function allows to perform invalidate on the view from outside
     * It is used to animate the view
     */
    public void invalidateViewForAnimation() {
        this.startTime = System.currentTimeMillis();
        this.postInvalidate();
    }

    /**
     * Draws polar grid
     * Grid contains the following:
     * 1. one circle with thick solid line with diameter 90% of the width of the canvas
     * 2. one circle with thick dotted line with diameter 45% of the width of the canvas
     * 3. one circle with thin line with diameter 45% of the width of the canvas
     * 4. one circle with thin line with diameter 22.5% of the width of the canvas
     * 5. one circle with thin line with diameter 67.5% of the width of the canvas
     * 6. a thin line passing through the center of the circle touching the circumference
     * of the circle at 0 degrees
     * 7. a thin line passing through the center of the circle touching the circumference
     * of the circle at 90 degrees
     * 8. a thin dotted line passing through the center of the circle touching the circumference
     * of the circle at 30 degrees
     * 9. a thin dotted line passing through the center of the circle touching the circumference
     * of the circle at 60 degrees
     * 10. a thin dotted line passing through the center of the circle touching the circumference
     * of the circle at 120 degrees
     * 11. a thin dotted line passing through the center of the circle touching the circumference
     * of the circle at 150 degrees
     * 12. a solid circle at the center of the canvas with radius 5% of the width of the canvas
     * 13. write the following text at the center of the canvas
     * 13.1. "0 degrees"
     * 13.2. "90 degrees"
     * 13.3. "30 degrees"
     * 13.4. "60 degrees"
     * 13.5. "120 degrees"
     * 13.6. "150 degrees"
     * 13.7. "180" degrees
     * 13.8. "210" degrees
     * 13.9. "240" degrees
     * 13.10. "300" degrees
     * 13.11. "330" degrees
     * 13.12. "270" degrees
     * @param canvas canvas object
     */
    private void drawPolarGrid(android.graphics.Canvas canvas) {
        //Make a paint object
        android.graphics.Paint thickLine = new android.graphics.Paint();
        android.graphics.Paint thinLine = new android.graphics.Paint();
        android.graphics.Paint thickDottedLine = new android.graphics.Paint();
        android.graphics.Paint thinDottedLine = new android.graphics.Paint();
        android.graphics.Paint solidCircle = new android.graphics.Paint();


        //Make the lines dark gray
        thickLine.setColor(android.graphics.Color.DKGRAY);
        thinLine.setColor(android.graphics.Color.DKGRAY);
        thickDottedLine.setColor(android.graphics.Color.DKGRAY);
        thinDottedLine.setColor(android.graphics.Color.DKGRAY);
        solidCircle.setColor(android.graphics.Color.DKGRAY);

        //Set the line width
        thickLine.setStrokeWidth(6);
        thinLine.setStrokeWidth(3);
        thickDottedLine.setStrokeWidth(6);
        thinDottedLine.setStrokeWidth(3);
        solidCircle.setStrokeWidth(2);


        //Set the line style
        thickLine.setStyle(android.graphics.Paint.Style.STROKE);
        thinLine.setStyle(android.graphics.Paint.Style.STROKE);
        thickDottedLine.setStyle(android.graphics.Paint.Style.STROKE);
        thinDottedLine.setStyle(android.graphics.Paint.Style.STROKE);
        solidCircle.setStyle(android.graphics.Paint.Style.FILL);

        //Set the line type
        thickLine.setPathEffect(null);
        thinLine.setPathEffect(null);
        thickDottedLine.setPathEffect(new android.graphics.DashPathEffect(new float[]{10, 20}, 0));
        thinDottedLine.setPathEffect(new android.graphics.DashPathEffect(new float[]{10, 20}, 0));
        solidCircle.setPathEffect(null);

        //Draw the circles
        //Circle described in point 1
        canvas.drawCircle(canvasStats.centerX, canvasStats.centerY, (float) (canvasStats.width * 0.9/2f), thickLine);
        //Circle described in point 2
        canvas.drawCircle(canvasStats.centerX, canvasStats.centerY, (float) (canvasStats.width * 0.45/2f), thickDottedLine);
        //Circle described in point 3
        canvas.drawCircle(canvasStats.centerX, canvasStats.centerY, (float) (canvasStats.width * 0.45/2f), thinLine);
        //Circle described in point 4
        canvas.drawCircle(canvasStats.centerX, canvasStats.centerY, (float) (canvasStats.width * 0.225/2f), thinLine);
        //Circle described in point 5
        canvas.drawCircle(canvasStats.centerX, canvasStats.centerY, (float) (canvasStats.width * 0.675/2f), thinLine);
        //Circle described in point 12
        canvas.drawCircle(canvasStats.centerX, canvasStats.centerY, (float) (canvasStats.width * 0.05/2f), solidCircle);

        //Draw the lines
        //Line described in point 6
        //Path is used to draw the line because line cannot be drawn with a dash path effect
        android.graphics.Path path = new android.graphics.Path();
        path.moveTo(canvasStats.centerX - (float) (canvasStats.width * 0.45), canvasStats.centerY);
        path.lineTo(canvasStats.centerX + (float) (canvasStats.width * 0.45), canvasStats.centerY);
        path.close();
        canvas.drawPath(path, thinLine);
        //Line described in point 7
        path = new android.graphics.Path();
        path.moveTo(canvasStats.centerX, canvasStats.centerY - (float) (canvasStats.width * 0.45));
        path.lineTo(canvasStats.centerX, canvasStats.centerY + (float) (canvasStats.width * 0.45));
        path.close();
        canvas.drawPath(path, thinLine);
        //Line described in point 8
        path = new android.graphics.Path();
        path.moveTo(canvasStats.centerX - (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(30))), canvasStats.centerY - (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(30))));
        path.lineTo(canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(30))), canvasStats.centerY + (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(30))));
        path.close();
        canvas.drawPath(path, thinDottedLine);
        //Line described in point 9
        path = new android.graphics.Path();
        path.moveTo(canvasStats.centerX - (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(60))), canvasStats.centerY - (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(60))));
        path.lineTo(canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(60))), canvasStats.centerY + (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(60))));
        path.close();
        canvas.drawPath(path, thinDottedLine);
        //Line described in point 10
        path = new android.graphics.Path();
        path.moveTo(canvasStats.centerX - (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(120))), canvasStats.centerY - (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(120))));
        path.lineTo(canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(120))), canvasStats.centerY + (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(120))));
        path.close();
        canvas.drawPath(path, thinDottedLine);
        //Line described in point 11
        path = new android.graphics.Path();
        path.moveTo(canvasStats.centerX - (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(150))), canvasStats.centerY - (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(150))));
        path.lineTo(canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(150))), canvasStats.centerY + (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(150))));
        path.close();
        canvas.drawPath(path, thinDottedLine);

        //Draw the text
        //Text described in point 13.1
        placeTextOnCanvas(canvas, "0\u00B0",
                canvasStats.centerX + (float) (canvasStats.width * 0.9/2f) + dpToPixels(5),
                canvasStats.centerY, Paint.Align.LEFT, Paint.Align.CENTER);
        //Text described in point 13.2
        placeTextOnCanvas(canvas, "90\u00B0",
                canvasStats.centerX,
                canvasStats.centerY - (float) (canvasStats.width * 0.9/2f) - dpToPixels(5),
                Paint.Align.CENTER, Paint.Align.RIGHT);
        //Text described in point 13.3
        placeTextOnCanvas(canvas, "30\u00B0",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(30))),
                canvasStats.centerY - (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(30))) - dpToPixels(5),
                Paint.Align.LEFT, Paint.Align.RIGHT);
        //Text described in point 13.4
        placeTextOnCanvas(canvas, "60\u00B0",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(60))),
                canvasStats.centerY - (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(60))) - dpToPixels(5),
                Paint.Align.LEFT, Paint.Align.RIGHT);
        //Text described in point 13.5
        placeTextOnCanvas(canvas, "120\u00B0",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(120))),
                canvasStats.centerY - (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(120))) - dpToPixels(5),
                Paint.Align.RIGHT, Paint.Align.RIGHT);
        //Text described in point 13.6
        placeTextOnCanvas(canvas, "150\u00B0",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(150))),
                canvasStats.centerY - (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(150))) - dpToPixels(5),
                Paint.Align.RIGHT, Paint.Align.RIGHT);
        //Text described in point 13.7
        placeTextOnCanvas(canvas, "180\u00B0",
                canvasStats.centerX - (float) (canvasStats.width * 0.9/2f) - dpToPixels(5),
                canvasStats.centerY, Paint.Align.CENTER, Paint.Align.RIGHT, 270);
        //Text described in point 13.8
        placeTextOnCanvas(canvas, "210\u00B0",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(150))),
                canvasStats.centerY + (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(150))) + dpToPixels(5),
                Paint.Align.RIGHT, Paint.Align.LEFT);
        //Text described in point 13.9
        placeTextOnCanvas(canvas, "240\u00B0",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(120))),
                canvasStats.centerY + (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(120))) + dpToPixels(5),
                Paint.Align.RIGHT, Paint.Align.LEFT);
        //Text described in point 13.10
        placeTextOnCanvas(canvas, "300\u00B0",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(60))),
                canvasStats.centerY + (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(60))) + dpToPixels(5),
                Paint.Align.LEFT, Paint.Align.LEFT);
        //Text described in point 13.11
        placeTextOnCanvas(canvas, "330\u00B0",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(30))),
                canvasStats.centerY + (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(30))) + dpToPixels(5),
                Paint.Align.LEFT, Paint.Align.LEFT);
        //Text described in point 13.12
        placeTextOnCanvas(canvas, "270\u00B0",
                canvasStats.centerX,
                canvasStats.centerY + (float) (canvasStats.width * 0.9/2f) + dpToPixels(5),
                Paint.Align.CENTER, Paint.Align.LEFT);
    }

    /**
     * This function converts dp to pixels
     * @param dp dp value
     * @return pixels
     */
    private int dpToPixels(double dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    /**
     * This function converts pixels to dp
     * @param pixels pixels
     *               @return dp value
     */
    private double pixelsToDp(int pixels) {
        return pixels / getResources().getDisplayMetrics().density;
    }

    /**
     * This function places the text on canvas at a specified point
     * allowing to specify the horizontal and vertical alignment
     * @param canvas canvas object
     * @param text text to be placed
     * @param x x coordinate of the center point of the text
     * @param y y coordinate of the center point of the text
     * @param horizontalAlignment horizontal alignment of the text
     * @param verticalAlignment vertical alignment of the text
     */
    private void placeTextOnCanvas(android.graphics.Canvas canvas, String text, float x, float y, android.graphics.Paint.Align horizontalAlignment, android.graphics.Paint.Align verticalAlignment) {
        /*//Make a paint object
        android.graphics.Paint paintForText = new android.graphics.Paint();
        //Make text color black
        paintForText.setColor(android.graphics.Color.BLACK);
        //Set the text size
        paintForText.setTextSize(dpToPixels(fontSizeDpGridText));

        // Calculate the horizontal offset for the text based on the horizontal alignment
        float xOffset = 0;
        if (horizontalAlignment == android.graphics.Paint.Align.LEFT) {
            xOffset = 0;
        } else if (horizontalAlignment == android.graphics.Paint.Align.CENTER) {
            xOffset = paintForText.measureText(text)/2;
        } else if (horizontalAlignment == android.graphics.Paint.Align.RIGHT) {
            xOffset = paintForText.measureText(text);
        }
        // Calculate the vertical offset for the text based on the vertical alignment
        float yOffset = 0;
        if (verticalAlignment == android.graphics.Paint.Align.LEFT) {
            yOffset = paintForText.getTextSize();
        } else if (verticalAlignment == android.graphics.Paint.Align.CENTER) {
            yOffset = paintForText.getTextSize()/2;
        } else if (verticalAlignment == android.graphics.Paint.Align.RIGHT) {
            yOffset = 0;
        }
        //Draw the text
        canvas.drawText(text, x - xOffset, y + yOffset, paintForText);

         */
        placeTextOnCanvas(canvas, text, x, y, horizontalAlignment, verticalAlignment, 0);
    }

    /**
     * This function places the text on canvas at a specified point
     * allowing to specify the horizontal and vertical alignment
     * also allowing to specify rotation of the text
     * @param canvas canvas object
     * @param text text to be placed
     * @param x x coordinate of the center point of the text
     * @param y y coordinate of the center point of the text
     * @param horizontalAlignment horizontal alignment of the text
     * @param verticalAlignment vertical alignment of the text
     * @param rotation rotation of the text
     */
    private void placeTextOnCanvas(android.graphics.Canvas canvas, String text, float x, float y, android.graphics.Paint.Align horizontalAlignment, android.graphics.Paint.Align verticalAlignment, float rotation) {
        //Make a paint object
        android.graphics.Paint paintForText = new android.graphics.Paint();
        //Make text color black
        paintForText.setColor(android.graphics.Color.BLACK);
        //Set the text size
        paintForText.setTextSize(dpToPixels(fontSizeDpGridText));

        // Calculate the horizontal offset for the text based on the horizontal alignment
        float xOffset = 0;
        if (horizontalAlignment == android.graphics.Paint.Align.LEFT) {
            xOffset = 0;
        } else if (horizontalAlignment == android.graphics.Paint.Align.CENTER) {
            xOffset = paintForText.measureText(text) / 2;
        } else if (horizontalAlignment == android.graphics.Paint.Align.RIGHT) {
            xOffset = paintForText.measureText(text);
        }
        // Calculate the vertical offset for the text based on the vertical alignment
        float yOffset = 0;
        if (verticalAlignment == android.graphics.Paint.Align.LEFT) {
            yOffset = paintForText.getTextSize();
        } else if (verticalAlignment == android.graphics.Paint.Align.CENTER) {
            yOffset = paintForText.getTextSize() / 2;
        } else if (verticalAlignment == android.graphics.Paint.Align.RIGHT) {
            yOffset = 0;
        }
        //Draw the text
        canvas.save();
        canvas.rotate(rotation, x, y);
        canvas.drawText(text, x - xOffset, y + yOffset, paintForText);
        canvas.restore();
    }

    /**
     * This function places an arrow on the canvas
     * The arrow has a circle at the beginning and an arrow head at the end
     * The arrow head is a hallow triangle for currents
     * The arrow head is a filled triangle for voltages
     * @param canvas canvas object
     * @param length normalized length of the arrow
     * @param angle angle of the arrow
     * @param color color of the arrow
     * @param type type of the arrow
     */
    private void placeArrowOnCanvas(android.graphics.Canvas canvas, float length, float angle, int color, ArrowType type) {
        //Make a paint object
        android.graphics.Paint paintForArrow = new android.graphics.Paint();
        paintForArrow.setStyle(android.graphics.Paint.Style.STROKE);
        paintForArrow.setColor(color);
        paintForArrow.setStrokeWidth(dpToPixels(type == ArrowType.VOLTAGE ? strokeSizeDpVoltageArrow : strokeSizeDpCurrentArrow));
        paintForArrow.setPathEffect(null);

        //Paint the arrow head
        android.graphics.Paint paintForArrowHead = new android.graphics.Paint();
        paintForArrowHead.setColor(color);
        paintForArrowHead.setPathEffect(null);
        if (type == ArrowType.VOLTAGE) {
            paintForArrowHead.setStrokeWidth(dpToPixels(3));
            paintForArrowHead.setStyle(android.graphics.Paint.Style.FILL);
        } else if (type == ArrowType.CURRENT) {
            paintForArrowHead.setStrokeWidth(dpToPixels(3));
            paintForArrowHead.setStyle(Paint.Style.STROKE);
        }



        //Convert normalized length to pixels
        float lengthInPixels = length * canvasStats.width * 0.45f;

        //Calculate the coordinates of the arrow
        float x1 = canvasStats.centerX;
        float y1 = canvasStats.centerY;
        float x2 = canvasStats.centerX + lengthInPixels * (float) Math.cos(Math.toRadians(angle));
        float y2 = canvasStats.centerY - lengthInPixels * (float) Math.sin(Math.toRadians(angle));
        float x3 = x2 - lengthInPixels * 0.1f * (float) Math.cos(Math.toRadians(angle + 30));
        float y3 = y2 + lengthInPixels * 0.1f * (float) Math.sin(Math.toRadians(angle + 30));
        float x4 = x2 - lengthInPixels * 0.1f * (float) Math.cos(Math.toRadians(angle - 30));
        float y4 = y2 + lengthInPixels * 0.1f * (float) Math.sin(Math.toRadians(angle - 30));


        //Draw the arrow
        if (type == ArrowType.VOLTAGE) {
            //Draw the arrow
            canvas.drawLine(x1, y1, 0.5f*(x3+x4), 0.5f*(y3+y4), paintForArrow);
            //Draw the arrow head
            android.graphics.Path path = new android.graphics.Path();
            path.moveTo(x2, y2);
            path.lineTo(x3, y3);
            path.lineTo(x4, y4);
            path.close();
            canvas.drawPath(path, paintForArrowHead);
        } else if (type == ArrowType.CURRENT) {
            //Draw the arrow
            canvas.drawLine(x1, y1, 0.5f*(x3+x4), 0.5f*(y3+y4), paintForArrow);
            //Draw the arrow head
            android.graphics.Path path = new android.graphics.Path();
            path.moveTo(x2, y2);
            path.lineTo(x3, y3);
            path.lineTo(x4, y4);
            path.close();
            canvas.drawPath(path, paintForArrowHead);
        }
        //Draw the circle
        //Create paint object for the circle with the same color as the arrow
        // but with a fill style
        android.graphics.Paint paintForCircle = new android.graphics.Paint();
        paintForCircle.setStyle(android.graphics.Paint.Style.FILL);
        paintForCircle.setColor(color);
        paintForCircle.setStrokeWidth(1);
        paintForCircle.setPathEffect(null);
        //Draw the circle
        canvas.drawCircle(x1, y1, 0.025f*canvasStats.width, paintForCircle);
    }
}
