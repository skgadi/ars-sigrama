package mx.com.sigrama.ars.common;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;


/**
 * Created by SKGadi on 24/11/2023.
 * This class is used to store the encoded images
 * It takes images from resources and encodes them to base64
 */
public class EncodedImages {

    /**
     * This function takes argument as resource id and returns the encoded image
     * @param resourceId
     * @return
     *
     */
    public static String getEncodedImageForJPEG(Context context, int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
        byte[] byteArray = byteStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
