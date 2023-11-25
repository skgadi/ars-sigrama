package mx.com.sigrama.ars.common;

import android.graphics.Color;

/**
 * Created by SKGadi on 24/11/2023.
 * This class supplies the colors for the signals
 */
public class ColorsForSignals {
    enum COLOR_SCHEME {
        SCHEME_0,
        SCHEME_1,
        SCHEME_2,
        SCHEME_3,
    }
    int Colors[][] = {
            {Color.BLACK, Color.RED, Color.BLUE, Color.GRAY, Color.GREEN},
            {Color.BLACK, Color.RED, Color.GRAY, Color.BLUE, Color.GREEN},
            {Color.RED, Color.YELLOW, Color.BLUE, Color.BLACK, Color.GREEN},
            {Color.BLACK, Color.BLACK, Color.BLACK, Color.BLUE, Color.GREEN},
    };
    private COLOR_SCHEME selectedColorScheme = COLOR_SCHEME.SCHEME_0;
    public ColorsForSignals () {
    }
    public ColorsForSignals (COLOR_SCHEME colorScheme) {
        selectedColorScheme = colorScheme;
    }
    public int getColor(int channel) {
        return Colors[selectedColorScheme.ordinal()][channel];
    }
    public int getNumberOfColors() {
        return Colors[selectedColorScheme.ordinal()].length;
    }
    public int getNumberOfColorSchemes() {
        return Colors.length;
    }
    public void setColorScheme(COLOR_SCHEME colorScheme) {
        selectedColorScheme = colorScheme;
    }
    public COLOR_SCHEME getColorScheme() {
        return selectedColorScheme;
    }
    public int getColor(int channel, COLOR_SCHEME scheme) {
        return Colors[scheme.ordinal()][channel];
    }
    public int getColor(int channel, int colorScheme) {
        return Colors[colorScheme][channel];
    }
    public int getNumberOfColors(COLOR_SCHEME scheme) {
        return Colors[scheme.ordinal()].length;
    }
    public int getNumberOfColorSchemes(COLOR_SCHEME scheme) {
        return Colors.length;
    }
    public void setColorScheme(int colorScheme) {
        selectedColorScheme = COLOR_SCHEME.values()[colorScheme];
    }

    public String getColorHex(int channel) {
        return String.format("#%06X", (0xFFFFFF & Colors[selectedColorScheme.ordinal()][channel]));
    }

}
