package mx.com.sigrama.ars.RealtimeDisplays;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import mx.com.sigrama.ars.MainActivity;
import mx.com.sigrama.ars.R;
import mx.com.sigrama.ars.databinding.FragmentRealtimeDisplayOscilloscopeBinding;
import mx.com.sigrama.ars.device.ResampledData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayOscilloscope#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayOscilloscope extends Fragment {

    private MainActivity mainActivity;
    List<LineDataSet> datasets = new ArrayList<LineDataSet>();
    private FragmentRealtimeDisplayOscilloscopeBinding binder;
    public DisplayOscilloscope() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binder = FragmentRealtimeDisplayOscilloscopeBinding.inflate(inflater, container, false);


        // Get a reference to MainActivity
        mainActivity = (MainActivity) getActivity();

        //Create graph and update when data is posted

        binder.oscilloscopeChart.setDrawGridBackground(false);
        binder.oscilloscopeChart.getDescription().setEnabled(false);
        binder.oscilloscopeChart.setDrawBorders(true);
        binder.oscilloscopeChart.getAxisLeft().setEnabled(true);
        binder.oscilloscopeChart.getAxisRight().setEnabled(true);
        binder.oscilloscopeChart.getXAxis().setEnabled(true);
        binder.oscilloscopeChart.getXAxis().setDrawGridLines(true);
        binder.oscilloscopeChart.getAxisLeft().setDrawGridLines(true);
        binder.oscilloscopeChart.getAxisRight().setDrawGridLines(true);
        binder.oscilloscopeChart.getAxisLeft().setDrawAxisLine(true);
        binder.oscilloscopeChart.getAxisRight().setDrawAxisLine(true);
        binder.oscilloscopeChart.getXAxis().setDrawAxisLine(true);
        binder.oscilloscopeChart.getAxisLeft().setDrawLabels(true);
        binder.oscilloscopeChart.getAxisRight().setDrawLabels(true);
        binder.oscilloscopeChart.getXAxis().setDrawLabels(true);
        binder.oscilloscopeChart.getLegend().setEnabled(true);
        binder.oscilloscopeChart.setTouchEnabled(true);
        binder.oscilloscopeChart.setDragEnabled(true);
        binder.oscilloscopeChart.setScaleEnabled(true);
        binder.oscilloscopeChart.setPinchZoom(true);
        binder.oscilloscopeChart.setDoubleTapToZoomEnabled(true);
        binder.oscilloscopeChart.setHighlightPerDragEnabled(true);
        binder.oscilloscopeChart.setHighlightPerTapEnabled(true);
        binder.oscilloscopeChart.setAutoScaleMinMaxEnabled(true);
        binder.oscilloscopeChart.setKeepPositionOnRotation(true);
        binder.oscilloscopeChart.setNoDataText("No se recibó los datos");
        //add a chart with some random data



        mainActivity.signalConditioningAndProcessing.getOscilloscopeData().observe(getViewLifecycleOwner(), new Observer<ResampledData>() {
            @Override
            public void onChanged(ResampledData resampledData) {
                //Log.d("SKGadi", "Updating the Oscilloscope's display");
                if (resampledData==null) {
                    //Log.d("SKGadi", "Null for displaying the Oscilloscope");
                    return;
                }
                ResampledData.DATA graphData = resampledData.obtainDataForOscilloscope();
                binder.oscilloscopeChart.clear();
                if (graphData == null || graphData.getRESAMPLE_SIZE() == 0) {
                    binder.oscilloscopeChart.invalidate();
                    //Log.d("SKGadi", "Empty for displaying the Oscilloscope");
                    return;
                }
                //Log.d("SKGadi", "Not empty for displaying the Oscilloscope");
                //Log.d("SKGadi", "onChanged: " + graphData.getRESAMPLE_SIZE());
                LineData data = new LineData();
                datasets.removeAll(datasets);
                for (int i=0; i<graphData.getNO_OF_CHANNELS(); i++) {
                    datasets.add(new LineDataSet(new ArrayList<Entry>(), graphData.getChannelName(i)));
                    for (int j=0; j<graphData.getRESAMPLE_SIZE(); j++) {
                        datasets.get(i).addEntry(new Entry((float)graphData.getDatum(j).getT()*1000, (float)graphData.getDatum(j).getY(i)));
                    }
                    datasets.get(i).setDrawCircles(false);
                    datasets.get(i).setDrawCircleHole(false);
                    datasets.get(i).setDrawValues(false);
                    datasets.get(i).setLineWidth(1f);
                    datasets.get(i).setColor(graphData.getChannelColor(i));
                    datasets.get(i).setAxisDependency(YAxis.AxisDependency.LEFT);
                    data.addDataSet(datasets.get(i));
                }


                binder.oscilloscopeChart.setData(data);
                binder.oscilloscopeChart.invalidate();
                binder.oscilloscopeChart.animateX(4000);
                binder.oscilloscopeChart.invalidate();
            }
        });
        return binder.getRoot();
    }
}