package com.joneill.sidebysideviewer.image;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.File;

/**
 * Created by josep on 2/14/2017.
 */
public interface CompareContract {
    interface View {
        void openImage(int position);
        void showNewImage(Uri imageUri, int position);
        void hideImage(int imageViewId);
        void updateImage(Bitmap image, int position);
        void updateRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter);
        void showMessage();
        void hideMessage();
        void showToolsbar(android.view.View toolsbar, boolean animate);
        void hideToolsbar(android.view.View toolsbar, boolean animate);
        void showFab();
        void hideFab();
        void showSaveDialog();
        void scanMedia(File outputFile);
    }

    interface UserActionInterface {
        void addNewImage();
        void replaceImage(int position);
        void mirrorImage(Bitmap image, int position);
        void changeToolsbarVisibility(android.view.View toolsbar, boolean animate);
        void openSaveDialog();
        void saveImage(String fileName, Bitmap image);
        void onImageAvailable(Uri imageUri, int position);
        void onImageUnavailable();
        void rotateImageCW(Bitmap image, int position);
        void rotateImageCCW(Bitmap image, int position);
        void changeOrientation();
        Bitmap combineImages(int orientation, android.view.View image1, android.view.View image2);
    }
}
