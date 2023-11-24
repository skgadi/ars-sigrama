package mx.com.sigrama.ars.RealtimeDisplays;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.math3.complex.Complex;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import mx.com.sigrama.ars.MainActivity;
import mx.com.sigrama.ars.common.PhasorDiagram;
import mx.com.sigrama.ars.databinding.FragmentRealtimeDisplayPhasesBinding;


public class DisplayPhases extends Fragment {

    private MainActivity mainActivity;
    private FragmentRealtimeDisplayPhasesBinding binder;

    public DisplayPhases() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binder = FragmentRealtimeDisplayPhasesBinding.inflate(inflater, container, false);

        // Get a reference to MainActivity
        mainActivity = (MainActivity) getActivity();

        //Update binder.fragmentRealtimeDisplayPhasesDiagram when
        //mainActivity.signalConditioningAndProcessing.getPhasorData() is available
        mainActivity.signalConditioningAndProcessing.getPhasorData().observe(getViewLifecycleOwner(), phasorData -> {

            binder.fragmentRealtimeDisplayPhasesDiagram.invalidateViewForAnimation(phasorData);

            // Update the WebView with the phasor data
            // The string contains HTML code that displays the phasor data
            if (phasorData == null) return;
            if (phasorData.getFftDataForHarmonics() == null) return;
            if (phasorData.getFftDataForHarmonics().length == 0) return;
            if (phasorData.getFftDataForHarmonics()[0].length == 0) return;
            //Complex[][] harmonics = phasorData.getFftDataForHarmonics();
            String html = "<html><style>body,table{text-align:center;margin-left: auto; margin-right: auto;} table {border-collapse: collapse;}</style><body>"
                    + "<p>Frequency: <b>"+String.format(Locale.ENGLISH, "%.0f", phasorData.getFundamentalFrequency())+" Hz</b></p>"
                    + "<table border=\"1\" style=\"width:100%\">"
                    + "<tr><th>Parameter</th><th>Amplitude</th><th>Phase</th></tr>"
                    + "<tr><td style=\"text-align:center\">V<sub>1-E</sub></td><td style=\"text-align:right\">"+String.format(Locale.ENGLISH, "%.0f V ", phasorData.getFundamentalValue(0).abs()/Math.sqrt(2))+"</td><td style=\"text-align:right\">"+String.format(Locale.ENGLISH, "%.0f°", phasorData.getFundamentalValue(0).getArgument()*180/Math.PI)+"</td></tr>"
                    + "<tr><td style=\"text-align:center\">V<sub>2-E</sub></td><td style=\"text-align:right\">"+String.format(Locale.ENGLISH, "%.0f V ", phasorData.getFundamentalValue(1).abs()/Math.sqrt(2))+"</td><td style=\"text-align:right\">"+String.format(Locale.ENGLISH, "%.0f°", phasorData.getFundamentalValue(1).getArgument()*180/Math.PI)+"</td></tr>"
                    + "<tr><td style=\"text-align:center\">V<sub>3-E</sub></td><td style=\"text-align:right\">"+String.format(Locale.ENGLISH, "%.0f V ", phasorData.getFundamentalValue(2).abs()/Math.sqrt(2))+"</td><td style=\"text-align:right\">"+String.format(Locale.ENGLISH, "%.0f°", phasorData.getFundamentalValue(2).getArgument()*180/Math.PI)+"</td></tr>"
                    + "<tr><td style=\"text-align:center\">I<sub>1-E</sub></td><td style=\"text-align:right\">"+String.format(Locale.ENGLISH, "%.0f mA", phasorData.getFundamentalValue(3).abs()/Math.sqrt(2))+"</td><td style=\"text-align:right\">"+String.format(Locale.ENGLISH, "%.0f°", phasorData.getFundamentalValue(3).getArgument()*180/Math.PI)+"</td></tr>"
                    + "<tr><td style=\"text-align:center\">I<sub>2-E</sub></td><td style=\"text-align:right\">"+String.format(Locale.ENGLISH, "%.0f mA", phasorData.getFundamentalValue(4).abs()/Math.sqrt(2))+"</td><td style=\"text-align:right\">"+String.format(Locale.ENGLISH, "%.0f°", phasorData.getFundamentalValue(4).getArgument()*180/Math.PI)+"</td></tr>"
                    + "<tr><td style=\"text-align:center\">I<sub>3-E</sub></td><td style=\"text-align:right\">"+String.format(Locale.ENGLISH, "%.0f mA", phasorData.getFundamentalValue(5).abs()/Math.sqrt(2))+"</td><td style=\"text-align:right\">"+String.format(Locale.ENGLISH, "%.0f°", phasorData.getFundamentalValue(5).getArgument()*180/Math.PI)+"</td></tr>"
                    + "</table>"
                    +"</body></html>";
            binder.fragmentRealtimeDisplayPhasesInfo.loadData(html, "text/html", null);
        });





        return binder.getRoot();
    }

}