package mx.com.sigrama.ars.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import org.apache.commons.math3.complex.Complex;

import mx.com.sigrama.ars.device.SpectrumAnalysis;

/**
 * Created by SKGadi on 7th November 2023
 * Allows to generate view of a phasor diagram
 * Takes input data such as number of phases, voltage, current
 * Generates a phasor diagram
 */
public class PhasorDiagram extends View {
    int NO_OF_PHASES = 3;
    int [] colors = {Color.BLACK, Color.RED, Color.BLUE};
    Complex [] voltages;
    Complex[] currents;
    Complex[] previousVoltages;
    Complex[] previousCurrents;
    Complex[] presentVoltages;
    Complex[] presentCurrents;
    int scaleForVoltage = 100;
    int scaleForCurrent = 100;
    double fontSizeDpGridText = 10; // font size in dp for grid text
    double fontSizeDpScaleText = 20; // font size in dp for scale text
    double strokeSizeDpVoltageArrow = 7; // stroke size in dp for voltage arrow
    double strokeSizeDpCurrentArrow = 5; // stroke size in dp for current arrow

    int angleOfPhaseSequenceDot = 0; // angle of the phase sequence dot in degrees
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
    private int FRAMES_PER_SECOND = 30;
    private long ANIMATION_DURATION = 1000;
    private long timeSinceStart = 0;

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


        //Make background white
        canvas.drawColor(Color.WHITE);

        // Draw circles for showing polar grid
        drawPolarGrid(canvas);

        // Update the present values for voltages and currents
        calculatePresentValuesForVoltagesAndCurrents();

        // Obtain scale for voltage and current
        obtainScaleForVoltageAndCurrent();

        // Draw the scale
        displayVoltageAndCurrentScale(canvas);

        // Draw the arrows
        //Draw the currents
        for (int i=0; i<NO_OF_PHASES; i++) {
            placeArrowOnCanvas(canvas, (float) (presentCurrents[i].abs()/scaleForCurrent), (float) (presentCurrents[i].getArgument()*180/Math.PI), colors[i], ArrowType.CURRENT);
        }
        //Draw the voltages
        for (int i=0; i<NO_OF_PHASES; i++) {
            placeArrowOnCanvas(canvas, (float) (presentVoltages[i].abs()/scaleForVoltage), (float) (presentVoltages[i].getArgument()*180/Math.PI), colors[i], ArrowType.VOLTAGE);
        }

        //Display the sequence of the phasors
        showPhaseSequence(canvas);


        //Animate the view
        if (timeSinceStart < ANIMATION_DURATION) {
            this.postInvalidateDelayed( 1000 / FRAMES_PER_SECOND);
        }

    }

    /**
     * This function allows to perform invalidate on the view from outside
     * It is used to animate the view
     */
    public void invalidateViewForAnimation(SpectrumAnalysis spectrumAnalysis) {
        //Initialize the present voltages and currents
        if (previousVoltages == null) {
            previousVoltages = new Complex[NO_OF_PHASES];
            //Initialize the present voltages to 0
            for (int i=0; i<NO_OF_PHASES; i++) {
                previousVoltages[i] = new Complex(0, 0);
            }
        }
        if (previousCurrents == null) {
            previousCurrents = new Complex[NO_OF_PHASES];
            //Initialize the present currents to 0
            for (int i=0; i<NO_OF_PHASES; i++) {
                previousCurrents[i] = new Complex(0, 0);
            }
        }
        //extract the voltages and currents from spectrumAnalysis
        //Initialize voltages
        voltages = new Complex[NO_OF_PHASES];
        for (int i=0; i<NO_OF_PHASES; i++) {
            voltages[i] = new Complex(0, 0);
            if (spectrumAnalysis != null) {
                Complex tempValue = spectrumAnalysis.getFundamentalValue(i);
                if (tempValue != null) {
                    // use the RMS value for the display
                    voltages[i] = tempValue.divide(Math.sqrt(2));
                }
            }
        }

        //Initialize currents
        currents = new Complex[NO_OF_PHASES];
        for (int i=0; i<NO_OF_PHASES; i++) {
            currents[i] = new Complex(0, 0);
            if (spectrumAnalysis != null) {
                Complex tempValue = spectrumAnalysis.getFundamentalValue(i + NO_OF_PHASES);
                if (tempValue != null) {
                    // use the RMS value for the display
                    currents[i] = tempValue.divide(Math.sqrt(2));
                }
            }
        }
        //Change the start time to current time for animation
        startTime = System.currentTimeMillis();

        this.postInvalidate();
    }

    /**
     * This function obtains scale for voltage and current for the phasor diagram
     * The scale should be multiple of 10 for values less than 100
     * The scale should be multiple of 50 for values greater than 100
     * Takes no parameters and returns void
     *
     */
    private void obtainScaleForVoltageAndCurrent() {
        //Initialize the scale for voltage and current
        scaleForVoltage = 100;
        scaleForCurrent = 100;
        //Find the maximum voltage
        double maxVoltage = 0;
        for (int i = 0; i < NO_OF_PHASES; i++) {
            if (voltages[i].abs() > maxVoltage) {
                maxVoltage = voltages[i].abs();
            }
        }
        //Find the maximum current
        double maxCurrent = 0;
        for (int i = 0; i < NO_OF_PHASES; i++) {
            if (currents[i].abs() > maxCurrent) {
                maxCurrent = currents[i].abs();
            }
        }


        //Find the scale for voltage
        if (maxVoltage < 100) {
            scaleForVoltage = (int) Math.ceil(maxVoltage / 10d) * 10;
        } else {
            scaleForVoltage = (int) Math.ceil(maxVoltage / 50d) * 50;
        }
        //Find the scale for current
        if (maxCurrent < 100) {
            scaleForCurrent = (int) Math.ceil(maxCurrent / 10d) * 10;
        } else {
            scaleForCurrent = (int) Math.ceil(maxCurrent / 50d) * 50;
        }
        Log.d("SKGadi", "Max voltage: " + maxVoltage + " Scale: " + scaleForVoltage);
        Log.d("SKGadi", "Max current: " + maxCurrent + " Scale: " + scaleForCurrent);
    }

    /**
     * This function calculates the present values for voltages and currents
     * based on the animation time. It will help to smoothly move the arrows.
     * The animation duration is 1 second
     * It moves the arrow from previous values to the actual values
     * After 1 second, the arrow will be at the actual values and the previous values will be updated
     *
     * Takes no parameters and returns void
     *
     */
    private void calculatePresentValuesForVoltagesAndCurrents() {
        //Initialize the present voltages and currents
        if (presentVoltages == null) {
            presentVoltages = new Complex[NO_OF_PHASES];
            //Initialize the present voltages to 0
            for (int i = 0; i < NO_OF_PHASES; i++) {
                presentVoltages[i] = new Complex(0, 0);
            }
        }
        if (presentCurrents == null) {
            presentCurrents = new Complex[NO_OF_PHASES];
            //Initialize the present currents to 0
            for (int i = 0; i < NO_OF_PHASES; i++) {
                presentCurrents[i] = new Complex(0, 0);
            }
        }
        //Calculate the present values
        //Time since start of animation
        timeSinceStart = System.currentTimeMillis() - startTime;
        // The maximum value of time since start is 1000
        if (timeSinceStart > ANIMATION_DURATION) {
            timeSinceStart = ANIMATION_DURATION;
        }
        //Calculate the present values
        for (int i = 0; i < NO_OF_PHASES; i++) {
            presentVoltages[i] = previousVoltages[i].multiply(1 - timeSinceStart / 1000f).add(voltages[i].multiply(timeSinceStart / 1000f));
            presentCurrents[i] = previousCurrents[i].multiply(1 - timeSinceStart / 1000f).add(currents[i].multiply(timeSinceStart / 1000f));
        }
        //Update the previous values if the animation is complete
        if (timeSinceStart >= ANIMATION_DURATION) {
            previousVoltages = voltages;
            previousCurrents = currents;
        }
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
        placeTextOnCanvas(canvas, "0°",
                canvasStats.centerX + (float) (canvasStats.width * 0.9/2f) + dpToPixels(5),
                canvasStats.centerY, Paint.Align.LEFT, Paint.Align.CENTER, fontSizeDpGridText);
        //Text described in point 13.2
        placeTextOnCanvas(canvas, "90°",
                canvasStats.centerX,
                canvasStats.centerY - (float) (canvasStats.width * 0.9/2f) - dpToPixels(5),
                Paint.Align.CENTER, Paint.Align.RIGHT, fontSizeDpGridText);
        //Text described in point 13.3
        placeTextOnCanvas(canvas, "30°",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(30))),
                canvasStats.centerY - (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(30))) - dpToPixels(5),
                Paint.Align.LEFT, Paint.Align.RIGHT, fontSizeDpGridText);
        //Text described in point 13.4
        placeTextOnCanvas(canvas, "60°",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(60))),
                canvasStats.centerY - (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(60))) - dpToPixels(5),
                Paint.Align.LEFT, Paint.Align.RIGHT, fontSizeDpGridText);
        //Text described in point 13.5
        placeTextOnCanvas(canvas, "120°",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(120))),
                canvasStats.centerY - (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(120))) - dpToPixels(5),
                Paint.Align.RIGHT, Paint.Align.RIGHT, fontSizeDpGridText);
        //Text described in point 13.6
        placeTextOnCanvas(canvas, "150°",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(150))),
                canvasStats.centerY - (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(150))) - dpToPixels(5),
                Paint.Align.RIGHT, Paint.Align.RIGHT, fontSizeDpGridText);
        //Text described in point 13.7
        placeTextOnCanvas(canvas, "180°",
                canvasStats.centerX - (float) (canvasStats.width * 0.9/2f) - dpToPixels(5),
                canvasStats.centerY, Paint.Align.CENTER, Paint.Align.RIGHT, 270, fontSizeDpGridText);
        //Text described in point 13.8
        placeTextOnCanvas(canvas, "210°",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(150))),
                canvasStats.centerY + (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(150))) + dpToPixels(5),
                Paint.Align.RIGHT, Paint.Align.LEFT, fontSizeDpGridText);
        //Text described in point 13.9
        placeTextOnCanvas(canvas, "240°",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(120))),
                canvasStats.centerY + (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(120))) + dpToPixels(5),
                Paint.Align.RIGHT, Paint.Align.LEFT, fontSizeDpGridText);
        //Text described in point 13.10
        placeTextOnCanvas(canvas, "300°",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(60))),
                canvasStats.centerY + (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(60))) + dpToPixels(5),
                Paint.Align.LEFT, Paint.Align.LEFT, fontSizeDpGridText);
        //Text described in point 13.11
        placeTextOnCanvas(canvas, "330°",
                canvasStats.centerX + (float) (canvasStats.width * 0.45 * Math.cos(Math.toRadians(30))),
                canvasStats.centerY + (float) (canvasStats.width * 0.45 * Math.sin(Math.toRadians(30))) + dpToPixels(5),
                Paint.Align.LEFT, Paint.Align.LEFT, fontSizeDpGridText);
        //Text described in point 13.12
        placeTextOnCanvas(canvas, "270°",
                canvasStats.centerX,
                canvasStats.centerY + (float) (canvasStats.width * 0.9/2f) + dpToPixels(5),
                Paint.Align.CENTER, Paint.Align.LEFT, fontSizeDpGridText);
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
    private void placeTextOnCanvas(android.graphics.Canvas canvas, String text, float x, float y, android.graphics.Paint.Align horizontalAlignment, android.graphics.Paint.Align verticalAlignment, double textSize) {
        placeTextOnCanvas(canvas, text, x, y, horizontalAlignment, verticalAlignment, 0, textSize);
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
    private void placeTextOnCanvas(android.graphics.Canvas canvas, String text, float x, float y, android.graphics.Paint.Align horizontalAlignment, android.graphics.Paint.Align verticalAlignment, float rotation, double textSize) {
        //Make a paint object
        android.graphics.Paint paintForText = new android.graphics.Paint();
        //Make text color black
        paintForText.setColor(android.graphics.Color.BLACK);
        //Set the text size
        paintForText.setTextSize(dpToPixels(textSize));

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

    /**
     * This function displays voltage and current scale on the canvas
     * The voltage is displayed on top left corner
     * The current is displayed on top right corner
     */
    private void displayVoltageAndCurrentScale(android.graphics.Canvas canvas) {
        //Draw the text
        //Text for voltage
        placeTextOnCanvas(canvas, scaleForVoltage + " V",
                dpToPixels(10),
                dpToPixels(10),
                Paint.Align.LEFT, Paint.Align.LEFT, fontSizeDpScaleText);
        //Text for current
        placeTextOnCanvas(canvas, scaleForCurrent + " A",
                canvasStats.width - dpToPixels(10),
                dpToPixels(10),
                Paint.Align.RIGHT, Paint.Align.LEFT, fontSizeDpScaleText);
    }

    /**
     * Show phase sequence indicator on bottom right corner of the canvas
     * The voltage phase information is taken from variable voltages
     *
     * Phase sequence indicator contains the following:
     * 1. Circle showing clockwise of anticlockwise based on phase sequence
     * 2. Text showing + or - for the phase sequence
     * 3. Text ABC or ACB for the phase sequence
     * 4. Show an moving dot on the grid to indicate the phase sequence
     *
     * @param canvas canvas object
     *
     */
    private void showPhaseSequence(android.graphics.Canvas canvas) {

        //Obtain the sequence direction from the voltages
        boolean tempPositiveSequence = true;
        double angleForVoltage2 = voltages[1].getArgument()*180/Math.PI;
        if (angleForVoltage2 <0) {
            angleForVoltage2 += 360;
        }
        double angleForVoltage3 = voltages[2].getArgument()*180/Math.PI;
        if (angleForVoltage3 <0) {
            angleForVoltage3 += 360;
        }
        if (angleForVoltage2 > angleForVoltage3) {
            tempPositiveSequence = false;
        }


        //Make a paint object
        android.graphics.Paint paintForPhaseSequenceIndicator = new android.graphics.Paint();
        paintForPhaseSequenceIndicator.setStyle(android.graphics.Paint.Style.STROKE);
        paintForPhaseSequenceIndicator.setColor(android.graphics.Color.BLACK);
        paintForPhaseSequenceIndicator.setStrokeWidth(dpToPixels(3));
        paintForPhaseSequenceIndicator.setPathEffect(null);

        //Draw the circle
        //Create paint object for the circle with the same color as the arrow
        // but with a fill style
        int circleRadius = dpToPixels(30);
        int xyShift = dpToPixels(40);

        android.graphics.Paint paintForCircle = new android.graphics.Paint();
        paintForCircle.setStyle(Paint.Style.STROKE);
        paintForCircle.setColor(Color.BLACK);
        paintForCircle.setStrokeWidth(5);
        paintForCircle.setPathEffect(null);
        //Draw the circle
        canvas.drawCircle(canvasStats.centerX + canvasStats.width * 0.50f - xyShift,
                canvasStats.centerY + canvasStats.width * 0.50f - xyShift,
                circleRadius,
                paintForCircle);
        //Draw two arrow heads on the above circle to indicate the direction
        //Draw the arrow heads
        android.graphics.Paint paintForArrowHead = new android.graphics.Paint();
        paintForArrowHead.setColor(Color.BLACK);
        paintForArrowHead.setPathEffect(null);
        paintForArrowHead.setStrokeWidth(dpToPixels(3));
        paintForArrowHead.setStyle(Paint.Style.STROKE);
        // The arrow heads' direction changes based on the sequence
        // The arrow heads' direction is anticlockwise for positive sequence
        // The arrow heads' direction is clockwise for negative sequence
        // The arrow heads gives direction to the circle as per the sequence
        //calculating coordinates for the first arrow head
        float x1 = canvasStats.centerX + canvasStats.width * 0.50f - xyShift + circleRadius;
        float y1 = canvasStats.centerY + canvasStats.width * 0.50f - xyShift;
        float x2 = x1 + dpToPixels(10) * (float) Math.cos(Math.toRadians(tempPositiveSequence?230:50));
        float y2 = y1 - dpToPixels(10) * (float) Math.sin(Math.toRadians(tempPositiveSequence?230:50));
        float x3 = x1 - dpToPixels(10) * (float) Math.cos(Math.toRadians(tempPositiveSequence?140:310));
        float y3 = y1 + dpToPixels(10) * (float) Math.sin(Math.toRadians(tempPositiveSequence?140:310));
        //Draw the first arrow head
        canvas.drawLine(x1, y1, x2, y2, paintForArrowHead);
        canvas.drawLine(x1, y1, x3, y3, paintForArrowHead);

        //calculating coordinates for the second arrow head
        x1 = canvasStats.centerX + canvasStats.width * 0.50f - xyShift - circleRadius;
        y1 = canvasStats.centerY + canvasStats.width * 0.50f - xyShift;
        x2 = x1 + dpToPixels(10) * (float) Math.cos(Math.toRadians(tempPositiveSequence?50:230));
        y2 = y1 - dpToPixels(10) * (float) Math.sin(Math.toRadians(tempPositiveSequence?50:230));
        x3 = x1 - dpToPixels(10) * (float) Math.cos(Math.toRadians(tempPositiveSequence?310:140));
        y3 = y1 + dpToPixels(10) * (float) Math.sin(Math.toRadians(tempPositiveSequence?310:140));
        //Draw the second arrow head
        canvas.drawLine(x1, y1, x2, y2, paintForArrowHead);
        canvas.drawLine(x1, y1, x3, y3, paintForArrowHead);





        //Draw the text
        //Text for phase sequence
        placeTextOnCanvas(canvas, tempPositiveSequence?"ABC":"ACB",
                canvasStats.centerX + canvasStats.width * 0.50f - xyShift,
                canvasStats.centerY + canvasStats.width * 0.50f - xyShift,
                Paint.Align.CENTER, Paint.Align.CENTER, fontSizeDpScaleText);
        placeTextOnCanvas(canvas, tempPositiveSequence?"+":"-",
                canvasStats.centerX + canvasStats.width * 0.50f - xyShift,
                canvasStats.centerY + canvasStats.width * 0.50f - xyShift - dpToPixels(20),
                Paint.Align.CENTER, Paint.Align.CENTER, fontSizeDpScaleText);

        //Draw the moving dot
        //Draw only during the animation duration
        if (timeSinceStart < ANIMATION_DURATION) {
            // The dot is black in color filled with black color
            // The dot moves on the grid
            // The dot moves in the direction of the sequence

            Paint paintForDot = new Paint();
            paintForDot.setColor(Color.BLACK);
            paintForDot.setStyle(Paint.Style.FILL);
            paintForDot.setStrokeWidth(1);
            paintForDot.setPathEffect(null);


            //Calculate the coordinates of the dot
            //update the angle for the dot based on the phase sequence
            // During the animation duration, it should make three complete rotations
            // The angle of the dot should be 360 degrees after the animation duration
            // The angle of the dot should be 0 degrees at the start of the animation

            //Calculate the angle of the dot

            angleOfPhaseSequenceDot = (int) ((360f * timeSinceStart) / ANIMATION_DURATION);
            if (!tempPositiveSequence) {
                angleOfPhaseSequenceDot = 360 - angleOfPhaseSequenceDot;
            }





            //Calculate the coordinates of the dot
            //float gridRadius = (float) (canvasStats.width * 0.45/2f);
            //float x = canvasStats.centerX + gridRadius * (float) Math.cos(Math.toRadians(angleOfPhaseSequenceDot));
            //float y = canvasStats.centerY - gridRadius * (float) Math.sin(Math.toRadians(angleOfPhaseSequenceDot));

            float x = canvasStats.centerX + canvasStats.width * 0.50f - xyShift + circleRadius * (float) Math.cos(Math.toRadians(angleOfPhaseSequenceDot));
            float y = canvasStats.centerY + canvasStats.width * 0.50f - xyShift - circleRadius * (float) Math.sin(Math.toRadians(angleOfPhaseSequenceDot));
            //Draw the dot
            canvas.drawCircle(x, y, dpToPixels(5), paintForDot);
        }



    }
}
