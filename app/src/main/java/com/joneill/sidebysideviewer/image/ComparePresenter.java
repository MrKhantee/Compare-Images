package com.joneill.sidebysideviewer.image;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.joneill.sidebysideviewer.R;
import com.joneill.sidebysideviewer.utility.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by josep on 2/14/2017.
 */
public class ComparePresenter implements CompareContract.UserActionInterface {
    private CompareContract.View mCompareView;

    public ComparePresenter(CompareContract.View mCompareView) {
        this.mCompareView = mCompareView;
    }

    @Override
    public void addNewImage() {
        mCompareView.openImage(-1);
    }

    @Override
    public void replaceImage(int position) {
        mCompareView.openImage(position);
    }

    @Override
    public void onImageAvailable(Uri imageUri, int position) {
        if (imageUri != null) {
            mCompareView.hideMessage();
            mCompareView.showNewImage(imageUri, position);
        } else {
            onImageUnavailable();
        }
    }

    @Override
    public void onImageUnavailable() {

    }

    @Override
    public void mirrorImage(Bitmap image, int position) {
        image = Utils.mirrorBitmap(image);
        mCompareView.updateImage(image, position);
    }

    @Override
    public void rotateImageCW(Bitmap image, int position) {
        image = Utils.rotateBitmap(image, 90);
        mCompareView.updateImage(image, position);
    }

    @Override
    public void rotateImageCCW(Bitmap image, int position) {
        image = Utils.rotateBitmap(image, -90);
        mCompareView.updateImage(image, position);
    }

    @Override
    public void changeOrientation() {

    }

    @Override
    public Bitmap combineImages(int orientation, View image1, View image2) {
        Bitmap combinedBitmap = null;
        mCompareView.hideToolsbar(image1.findViewById(R.id.image_tools_bar), false);
        mCompareView.hideToolsbar(image2.findViewById(R.id.image_tools_bar), false);

        Bitmap b1 = Utils.createBitmapFromView(image1);
        Bitmap b2 = Utils.createBitmapFromView(image2);

        boolean isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT;
        combinedBitmap = Utils.combineBitmaps(isPortrait, b1, b2);
        if(!b1.isRecycled()) {
            b1.recycle();
        }
        if(!b2.isRecycled()) {
            b2.recycle();
        }
        return combinedBitmap;
    }

    @Override
    public void changeToolsbarVisibility(View toolsbar, boolean animate) {
        if (toolsbar.getVisibility() == View.VISIBLE) {
            mCompareView.hideToolsbar(toolsbar, animate);
        } else {
            mCompareView.showToolsbar(toolsbar, animate);
        }
    }

    @Override
    public void openSaveDialog() {
        mCompareView.showSaveDialog();
    }

    @Override
    public void saveImage(String fileName, Bitmap image) {
        if(fileName.isEmpty()) {
            fileName = String.valueOf(System.currentTimeMillis());
        }

        fileName += ".png";

        OutputStream os = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ File.separator + fileName);
            os = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, os);
            mCompareView.scanMedia(file);
        } catch (IOException e) {
            Log.e("IOException", "Problem saving bitmap", e);
        }
    }
}
