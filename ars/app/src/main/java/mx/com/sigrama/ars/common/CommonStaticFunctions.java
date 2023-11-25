package mx.com.sigrama.ars.common;

import android.webkit.WebView;

public class CommonStaticFunctions {
    // Calculate the % of scroll progress in the actual web page content
    public static float calculateProgression(WebView content) {
        float positionTopView = content.getTop();
        float contentHeight = content.getContentHeight();
        float currentScrollPosition = content.getScrollY();
        return (currentScrollPosition - positionTopView) / contentHeight;
    }
}
