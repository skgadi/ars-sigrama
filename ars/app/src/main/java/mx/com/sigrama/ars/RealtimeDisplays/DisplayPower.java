package mx.com.sigrama.ars.RealtimeDisplays;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mx.com.sigrama.ars.MainActivity;
import mx.com.sigrama.ars.databinding.FragmentRealtimeDisplayPowerBinding;

public class DisplayPower extends Fragment {

    MainActivity mainActivity;
    int yScrollPosition = 0;
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

            //Percentage of scroll progress in the actual web page content
            yScrollPosition = binder.powerSummaryWebview.getScrollY();


            //Updating Summary WebView with power quality data
            StringBuilder html = new StringBuilder("<html><style>body,table{text-align:center;margin-left: auto; margin-right: auto;} table {border-collapse: collapse;}</style><body>");
            html.append("<p>Summary</p>");
            html.append("<table border=\"1\">");
            html.append("<tr><th>Parameter</th><th>Value</th></tr>");
            html.append("<tr><td style=\"text-align:center\">VA</td><td style=\"text-align:right\">").append(String.format("%.2f", powerQuality.getApparentPower())).append(" VA</td></tr>");
            html.append("<tr><td style=\"text-align:center\">W</td><td style=\"text-align:right\">").append(String.format("%.2f", powerQuality.getActivePower())).append(" W</td></tr>");
            html.append("<tr><td style=\"text-align:center\">VAR</td><td style=\"text-align:right\">").append(String.format("%.2f", powerQuality.getReactivePower())).append(" VAR</td></tr>");
            html.append("<tr><td style=\"text-align:center\">PF</td><td style=\"text-align:right\">").append(String.format("%.2f", powerQuality.getPowerFactor())).append("</td></tr>");
            html.append("</table>");
            html.append("<p>Per phase data</p>");
            html.append("<table border=\"1\">");
            html.append("<tr><th>\u03D5</th><th>VA</th><th>W</th><th>VAR</th><th>PF</th></tr>");
            for (int i = 0; i < powerQuality.getNO_OF_PHASES(); i++) {
                html.append("<tr>");
                html.append("<td style=\"text-align:center\">").append(i + 1).append("</td>");
                html.append("<td style=\"text-align:right\">").append(String.format("%.0f", powerQuality.getApparentPower(i))).append("</td>");
                html.append("<td style=\"text-align:right\">").append(String.format("%.0f", powerQuality.getActivePower(i))).append("</td>");
                html.append("<td style=\"text-align:right\">").append(String.format("%.0f", powerQuality.getReactivePower(i))).append("</td>");
                html.append("<td style=\"text-align:right\">").append(String.format("%.2f", powerQuality.getPowerFactor(i))).append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");



            //Updating the details WebView
            //Contains table with the power quality data per phase and per harmonic in a tabular format

            html.append("<p>More details</p>");
            html.append("<table border=\"1\" style=\"width:100%\">");
            StringBuilder TableHeading = new StringBuilder("<tr><th rowspan=\"2\">H</th><th colspan=\"4\">\u03D5 - 1</th><th colspan=\"4\">\u03D5 - 2</th><th colspan=\"4\">\u03D5 - 3</th></tr>");
            TableHeading.append("<tr>");
            for (int i = 0; i < powerQuality.getNO_OF_PHASES(); i++) {
                TableHeading.append("<th>VA</th>");
                TableHeading.append("<th>W</th>");
                TableHeading.append("<th>VAR</th>");
                TableHeading.append("<th>cos(\u03D5)</th>");
            }
            TableHeading.append("</tr>");
            for (int i = 0; i < powerQuality.getNO_OF_HARMONICS(); i++) {
                if (i%10 == 0) html.append(TableHeading);

                html.append("<tr>");
                html.append("<td style=\"text-align:center\">H").append(i).append("</td>");
                for (int j = 0; j < powerQuality.getNO_OF_PHASES(); j++) {
                    html.append("<td style=\"text-align:right\">").append(String.format("%.0f", powerQuality.getApparentPower(j, i))).append("</td>");
                    html.append("<td style=\"text-align:right\">").append(String.format("%.0f", powerQuality.getActivePower(j, i))).append("</td>");
                    html.append("<td style=\"text-align:right\">").append(String.format("%.0f", powerQuality.getReactivePower(j, i))).append("</td>");
                    html.append("<td style=\"text-align:right\">").append(String.format("%.2f", powerQuality.getPowerFactor(j, i))).append("</td>");
                }
                html.append("</tr>");
            }
            html.append(TableHeading);
            html.append("</table>");
            html.append("</body></html>");
            binder.powerSummaryWebview.loadData(html.toString(), "text/html", null);
            //binder.powerDetailsWebview.loadData(html, "text/html", null);

            //Scrolling the webview to the previous position
            binder.powerSummaryWebview.post(() -> {
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        public void run() {
                            binder.powerSummaryWebview.scrollTo(0, yScrollPosition);
                        }
                    },
                    1000);
            });




        });



        return binder.getRoot();
    }
}