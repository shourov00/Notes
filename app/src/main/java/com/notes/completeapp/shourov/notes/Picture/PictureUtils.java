package com.notes.completeapp.shourov.notes.Picture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {

    //This method checks to see how big the screen is and then scales the image down to that siz
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }

    //get the size image and resize it
    public static Bitmap getScaledBitmap(String path, int destwidth, int destheight) {
        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSimpleSize = 1;
        if (srcHeight > destheight || srcWidth > destwidth) {
            float heightScale = srcHeight / destheight;
            float widthScale = srcWidth / destwidth;

            inSimpleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSimpleSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }
}
