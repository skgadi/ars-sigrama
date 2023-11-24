package mx.com.sigrama.ars.RealtimeDisplays;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.com.sigrama.ars.MainActivity;
import mx.com.sigrama.ars.databinding.FragmentRealtimeDisplayPowerBinding;

public class DisplayPower extends Fragment {

    MainActivity mainActivity;
    private FragmentRealtimeDisplayPowerBinding binder;
    public DisplayPower() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binder = FragmentRealtimeDisplayPowerBinding.inflate(inflater, container, false);


        // Get a reference to MainActivity
        mainActivity = (MainActivity) getActivity();

        //Observe power quality data
        mainActivity.signalConditioningAndProcessing.getPowerQualityData().observe(getViewLifecycleOwner(), powerQuality -> {
            if (powerQuality == null) return;
            if (!powerQuality.isDataValid()) return;
            //Updating Summary WebView with power quality data
            String html = "<html><style>body,table{text-align:center;margin-left: auto; margin-right: auto;} table {border-collapse: collapse;}</style><body>";
            html += "<p>Summary</p>";
            html += "<table border=\"1\">";
            html += "<tr><th>Parameter</th><th>Value</th></tr>";
            html += "<tr><td style=\"text-align:center\">VA</td><td style=\"text-align:right\">"+String.format("%.2f", powerQuality.getApparentPower())+" VA</td></tr>";
            html += "<tr><td style=\"text-align:center\">W</td><td style=\"text-align:right\">"+String.format("%.2f", powerQuality.getActivePower())+" W</td></tr>";
            html += "<tr><td style=\"text-align:center\">VAR</td><td style=\"text-align:right\">"+String.format("%.2f", powerQuality.getReactivePower())+" VAR</td></tr>";
            html += "<tr><td style=\"text-align:center\">PF</td><td style=\"text-align:right\">"+String.format("%.2f", powerQuality.getPowerFactor())+"</td></tr>";
            html += "</table>";
            html += "<p>Per phase data</p>";
            html += "<table border=\"1\">";
            html += "<tr><th>\u03D5</th><th>VA</th><th>W</th><th>VAR</th><th>PF</th></tr>";
            for (int i = 0; i < powerQuality.getNO_OF_PHASES(); i++) {
                html += "<tr>";
                html += "<td style=\"text-align:center\">"+(i+1)+"</td>";
                html += "<td style=\"text-align:right\">"+String.format("%.0f", powerQuality.getApparentPower(i))+"</td>";
                html += "<td style=\"text-align:right\">"+String.format("%.0f", powerQuality.getActivePower(i))+"</td>";
                html += "<td style=\"text-align:right\">"+String.format("%.0f", powerQuality.getReactivePower(i))+"</td>";
                html += "<td style=\"text-align:right\">"+String.format("%.2f", powerQuality.getPowerFactor(i))+"</td>";
                html += "</tr>";
            }
            html += "</table>";



            //Updating the details WebView
            //Contains table with the power quality data per phase and per harmonic in a tabular format

            html += "<p>More details</p>";
            html += "<table border=\"1\" style=\"width:100%\">";
            html += "<tr><th rowspan=\"2\">H</th><th colspan=\"4\">\u03D5 - 1</th><th colspan=\"4\">\u03D5 - 2</th><th colspan=\"4\">\u03D5 - 3</th></tr>";
            html += "<tr>";
            for (int i = 0; i < powerQuality.getNO_OF_PHASES(); i++) {
                html += "<th>VA</th>";
                html += "<th>W</th>";
                html += "<th>VAR</th>";
                html += "<th>cos(\u03D5)</th>";
            }
            html += "</tr>";
            for (int i = 0; i < powerQuality.getNO_OF_HARMONICS(); i++) {
                html += "<tr>";
                html += "<td style=\"text-align:center\">H"+(i)+"</td>";
                for (int j = 0; j < powerQuality.getNO_OF_PHASES(); j++) {
                    html += "<td style=\"text-align:right\">"+String.format("%.0f", powerQuality.getApparentPower(j, i))+"</td>";
                    html += "<td style=\"text-align:right\">"+String.format("%.0f", powerQuality.getActivePower(j, i))+"</td>";
                    html += "<td style=\"text-align:right\">"+String.format("%.0f", powerQuality.getReactivePower(j, i))+"</td>";
                    html += "<td style=\"text-align:right\">"+String.format("%.2f", powerQuality.getPowerFactor(j, i))+"</td>";
                }
                html += "</tr>";
            }
            html += "</table>";
            html += "</body></html>";
            binder.powerSummaryWebview.loadData(html, "text/html", null);
            //binder.powerDetailsWebview.loadData(html, "text/html", null);



        });



        return binder.getRoot();
    }
}