package com.joneill.sidebysideviewer.utility;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.joneill.sidebysideviewer.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by josep on 2/19/2017.
 */
public final class Utils {

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap mirrorBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap decodeBitmapFromUri(Uri uri, ContentResolver res, int inSampleSize) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = res.openInputStream(uri);
            bitmap = decodeSampledBitmapFromInputStream(in, res, uri, inSampleSize);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromInputStream(InputStream is, ContentResolver res, Uri uri,
                                                            int inSampleSize) throws IOException {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, new Rect(0, 0, 0, 0), options);

        options.inSampleSize = inSampleSize;

        options.inJustDecodeBounds = false;
        is.close();
        is = res.openInputStream(uri);
        return BitmapFactory.decodeStream(is, new Rect(0, 0, 0, 0), options);
    }

    public static Bitmap createBitmapFromView(View v) {
        if(v.getWidth() <= 0 || v.getHeight() <= 0) {
            Log.e("Bitmap Error", "Width or height cannot be 0");
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return bitmap;
    }

    public static Bitmap combineBitmaps(boolean isPortrait, Bitmap b1, Bitmap b2) {
        Bitmap combinedBitmap = null;
        int width, height = 0;

        if (isPortrait) {
            width = Math.max(b1.getWidth(), b2.getWidth());
            height = b1.getHeight() + b2.getHeight();
        } else {
            width = b1.getWidth() + b2.getWidth();
            height = Math.max(b1.getHeight(), b2.getHeight());
        }

        combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(combinedBitmap);

        canvas.drawBitmap(b1, 0f, 0f, null);
        if (isPortrait) {
            canvas.drawBitmap(b2, 0f, b2.getHeight(), null);
        } else {
            canvas.drawBitmap(b2, b2.getWidth(), 0f, null);
        }

        return combinedBitmap;
    }
}
