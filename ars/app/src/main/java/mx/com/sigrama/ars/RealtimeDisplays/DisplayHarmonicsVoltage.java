package mx.com.sigrama.ars.RealtimeDisplays;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.apache.commons.math3.complex.Complex;

import java.util.ArrayList;
import java.util.List;

import mx.com.sigrama.ars.MainActivity;
import mx.com.sigrama.ars.R;
import mx.com.sigrama.ars.common.formatterForHarmonicsXAxis;
import mx.com.sigrama.ars.databinding.FragmentRealtimeDisplayHarmonicsVoltageBinding;


public class DisplayHarmonicsVoltage extends Fragment {

    private MainActivity mainActivity;
    int yScrollPosition = 0;
    List<BarEntry>[] entriesGroup;
    private FragmentRealtimeDisplayHarmonicsVoltageBinding binder;
    public DisplayHarmonicsVoltage() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binder = FragmentRealtimeDisplayHarmonicsVoltageBinding.inflate(inflater, container, false);


        // Get a reference to MainActivity
        mainActivity = (MainActivity) getActivity();

        BarChart chart = binder.harmonicsVoltageChart;
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5);
        xAxis.setValueFormatter(new formatterForHarmonicsXAxis());
        entriesGroup = new ArrayList[3];

        //web view
        WebView webView = binder.harmonicsVoltageWebview;

        //Update graph when data is posted
        mainActivity.signalConditioningAndProcessing.getHarmonicsData().observe(getViewLifecycleOwner(), spectrumAnalysis -> {
            if (spectrumAnalysis == null) return;
            if (spectrumAnalysis.getFftDataForHarmonics() == null) return;
            if (spectrumAnalysis.getFftDataForHarmonics().length == 0) return;
            if (spectrumAnalysis.getFftDataForHarmonics()[0].length == 0) return;

            entriesGroup[0] = new ArrayList<>();
            entriesGroup[1] = new ArrayList<>();
            entriesGroup[2] = new ArrayList<>();

            for (int i = 2; i < 55; i++) {
                for (int j = 0; j < 3; j++) {
                    entriesGroup[j].add(new BarEntry(i, (float) spectrumAnalysis.getHarmonicsPercentage(j, i)));
                }
            }


            BarDataSet set1 = new BarDataSet(entriesGroup[0], "V1-E");
            BarDataSet set2 = new BarDataSet(entriesGroup[1], "V2-E");
            BarDataSet set3 = new BarDataSet(entriesGroup[2], "V3-E");

            float groupSpace = 0.2f;
            float barSpace = 0.0f; // x3 dataset
            float barWidth = 0.80f/3f; // x3 dataset
            // (0.00 + 0.80/3) * 3 + 0.2 = 1.00 -> interval per "group"

            set1.setColor(Color.BLACK);
            set2.setColor(Color.RED);
            set3.setColor(Color.BLUE);

            BarData data = new BarData(set1, set2, set3);
            data.setBarWidth(barWidth); // set the width of each bar
            chart.setData(data);
            chart.groupBars(1.5f, groupSpace, barSpace); // perform the "explicit" grouping
            chart.animate();
            chart.getDescription().setText("Voltage: THD for V1-E = " + String.format("%.1f", spectrumAnalysis.getTHD(0)) + "%, V2-E = " + String.format("%.1f", spectrumAnalysis.getTHD(1)) + "%, V3-E = " + String.format("%.1f", spectrumAnalysis.getTHD(2)) + "%");
            chart.invalidate(); // refresh

            // Update the WebView with the Harmonics data
            //Percentage of scroll progress in the actual web page content
            yScrollPosition = webView.getScrollY();

            // The string contains HTML code that displays the Harmonics data
            StringBuilder html = new StringBuilder("<html><style>body,table{text-align:center;margin-left: auto; margin-right: auto;} table {border-collapse: collapse;}</style><body>"
                    + "<p>Frequency: <b>" + String.format("%.0f", spectrumAnalysis.getFundamentalFrequency()) + " Hz</b></p>"
                    + "<table border=\"1\"  style=\"width:calc(100% - 8px); margin-right:8px;\">");
            String TableHeading =  "<tr><th>Parameter</th><th>V<sub>1-E</sub></th><th>V<sub>2-E</sub></th><th>V<sub>3-E</sub></th></tr>";
            html.append(TableHeading);
            //Adding the THD
            html.append("<tr><td style=\"text-align:center\">TDH</td>");
            for (int i=0; i<3; i++) {
                html.append("<td style=\"text-align:right; color:"+mainActivity.colorsForSignals.getColorHex(i)+"\">").append(String.format("%.1f", spectrumAnalysis.getTHD(i))).append("</td>");
            }
            html.append("</tr>");
            //Adding the crest factor (CF)
            html.append("<tr><td style=\"text-align:center\">CF</td>");
            for (int i=0; i<3; i++) {
                html.append("<td style=\"text-align:right; color:"+mainActivity.colorsForSignals.getColorHex(i)+"\">").append(String.format("%.1f", spectrumAnalysis.getCrestFactor(i))).append("</td>");
            }
            html.append("</tr>");

            for (int i=2; i<spectrumAnalysis.getFftDataForHarmonics()[0].length; i++) {
                if (i%10 == 0) html.append(TableHeading);
                html.append("<tr><td style=\"text-align:center\">H").append(i).append("</td>");
                for (int j=0; j<3; j++) {
                    html.append("<td style=\"text-align:right; color:"+mainActivity.colorsForSignals.getColorHex(j)+"\">").append(String.format("%.1f", spectrumAnalysis.getHarmonicsPercentage(j,i) )).append("</td>");
                }
                html.append("</tr>");
            }
            html.append(TableHeading);

            html.append("</table>")
                    .append("</body></html>");
            webView.loadData(html.toString(), "text/html", null);

            //Scrolling the webview to the previous position
            webView.post(() -> {
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {
                                webView.scrollTo(0, yScrollPosition);
                            }
                        },
                        500);
            });




        });


        return binder.getRoot();
    }
}