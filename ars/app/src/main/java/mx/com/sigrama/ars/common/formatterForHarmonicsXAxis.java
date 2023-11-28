package mx.com.sigrama.ars.common;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class formatterForHarmonicsXAxis extends ValueFormatter {

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return "H"+String.format("%.0f", value);
    }

}



/*
*
*
public class formatterForHarmonicsXAxis implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return "H"+value;
    }
}

*
* */