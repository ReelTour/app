package gaftech.reeltour.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by r.suleymanov on 19.04.2015.
 * email: ruslancer@gmail.com
 */

public class BitmapResizer {
    Context _context;
    public BitmapResizer(Context context) {
        _context = context;
    }
    public Bitmap getResizedBitmap(Bitmap image) {
        WindowManager wm = (WindowManager) _context.getSystemService(_context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        display.getMetrics(displaymetrics);
        int newHeight = displaymetrics.heightPixels;
        int newWidth = displaymetrics.widthPixels;

        int width = image.getWidth();
        int height = image.getHeight();

        float scaleWidth = 0;
        float scaleHeight = 0;
        if (width > height) {
            if (newWidth>newHeight) {
                scaleWidth = ((float) newWidth) / width;
                scaleHeight = ((float) newHeight) / height;
            } else {
                scaleWidth = ((float) newHeight) / width;
                scaleHeight = ((float) newWidth) / height;
            }
        } else {
            if (newWidth>newHeight) {
                scaleWidth = ((float) newHeight) / width;
                scaleHeight = ((float) newWidth) / height;
            } else {
                scaleWidth = ((float) newWidth) / width;
                scaleHeight = ((float) newHeight) / height;
            }
        }
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }
}
