package com.joneill.sidebysideviewer.image;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joneill.sidebysideviewer.R;
import com.joneill.sidebysideviewer.image.adapters.ImagesAdapter;
import com.joneill.sidebysideviewer.image.adapters.NoScrollGridLayoutManager;
import com.joneill.sidebysideviewer.utility.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by josep on 2/14/2017.
 */
public class CompareFragment extends Fragment implements CompareContract.View, OnClickListener {
    public static int INTENT_PICK_IMAGE = 1;
    private static final String INTENT_EXTRA_POSITION = ".position";

    private CompareContract.UserActionInterface mActionsListener;
    private RecyclerView mRecyclerView;
    private ImagesAdapter mImagesAdapter;
    private SharedPreferences mSharedPrefs;
    private FabState fabState;

    private int layoutOrientation;

    public static Fragment newInstance() {
        return new CompareFragment();
    }

    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_compare_layout, container, false);

        setupList(root);

        setFabState(FabState.OPEN_IMAGE);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_add_image);
        fab.setOnClickListener(this);

        return root;
    }

    private void setupList(View root) {
        mRecyclerView = (RecyclerView) root.findViewById(R.id.images_list);
        mRecyclerView.setAdapter(mImagesAdapter);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setNestedScrollingEnabled(false);

        int numColumns = getContext().getResources().getInteger(R.integer.images_max_columns);

        mRecyclerView.setHasFixedSize(true);
        layoutOrientation = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                GridLayoutManager.HORIZONTAL : GridLayoutManager.VERTICAL;
        mImagesAdapter.setOrientation(getResources().getConfiguration().orientation);
        mRecyclerView.setLayoutManager(new NoScrollGridLayoutManager(getContext(), numColumns, layoutOrientation, false));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionsListener = new ComparePresenter(this);
        mImagesAdapter = new ImagesAdapter(new ArrayList<Bitmap>(0), mItemListener);
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public void updateRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        recyclerView.removeAllViews();
        adapter.notifyDataSetChanged();
    }

    //Use -1 if position not needed
    @Override
    public void openImage(int position) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        //Push position parameter to the activity intent to receiver later
        Intent dataIntent = getActivity().getIntent() != null ? getActivity().getIntent() : new Intent();
        dataIntent.putExtra(INTENT_EXTRA_POSITION, position);
        getActivity().setIntent(dataIntent);

        startActivityForResult(intent, INTENT_PICK_IMAGE);
    }

    @Override
    public void showNewImage(Uri imageUri, int position) {
        Bitmap bitmap = null;
        int inSampledSize = Integer.valueOf(mSharedPrefs.getString(getString(R.string.pref_image_quality_key), "20"));
        bitmap = Utils.decodeBitmapFromUri(imageUri, getContext().getContentResolver(), inSampledSize);

        if (position == -1) {
            mImagesAdapter.addItem(bitmap);
        } else {
            //If I don't call this, the onTapListener stops working
            mRecyclerView.removeAllViews();
            mImagesAdapter.replaceItem(bitmap, position);
        }

        if (mImagesAdapter.getItemCount() > 1) {
            setFabState(FabState.SAVE_IMAGE);
            fab.setImageResource(R.drawable.content_save);
        }
    }

    @Override
    public void hideImage(int imageViewId) {

    }

    @Override
    public void updateImage(Bitmap image, int position) {
        mImagesAdapter.replaceItem(image, position);
        updateRecyclerView(mRecyclerView, mImagesAdapter);
    }

    @Override
    public void showMessage() {
        getView().findViewById(R.id.tv_message).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideMessage() {
        getView().findViewById(R.id.tv_message).setVisibility(View.INVISIBLE);
    }

    @Override
    public void showToolsbar(android.view.View toolsbar, boolean animate) {
        if (animate) {
            toolsbar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_up));
        }
        toolsbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideToolsbar(android.view.View toolsbar, boolean animate) {
        if (animate) {
            toolsbar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_down));
        }
        toolsbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showFab() {
        if (fab.getVisibility() == View.INVISIBLE) {
            fab.show();
        }
    }

    @Override
    public void hideFab() {
        if (fab.getVisibility() == View.VISIBLE) {
            fab.hide();
        }
    }

    @Override
    public void showSaveDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_save, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.save_image));

        final EditText et = (EditText) dialogLayout.findViewById(R.id.et_file_name);

        builder.setPositiveButton(getString(R.string.save_image), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String fileName = et.getText().toString();
                //Update the RecyclerView to the latest view before saving
                mImagesAdapter.notifyDataSetChanged();
                //mImagesAdapter.notifyDataSetChanged();
                Bitmap combinedBitmap = mActionsListener.combineImages(getResources().getConfiguration().orientation,
                        mRecyclerView.getChildAt(0), mRecyclerView.getChildAt(1));
                mActionsListener.saveImage(fileName, combinedBitmap);
                updateRecyclerView(mRecyclerView, mImagesAdapter);
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        builder.setView(dialogLayout);
        builder.show();
    }

    @Override
    public void scanMedia(File outputFile) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            final Uri contentUri = Uri.fromFile(outputFile);
            scanIntent.setData(contentUri);
            getActivity().sendBroadcast(scanIntent);
        } else {
            final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            getActivity().sendBroadcast(intent);
        }
    }

    private void setFabState(FabState fabState) {
        this.fabState = fabState;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri uri = Uri.parse("");
            int position = -1;
            if (data != null) {
                uri = data.getData();
                position = getActivity().getIntent().getIntExtra(INTENT_EXTRA_POSITION, -1);
            }
            mActionsListener.onImageAvailable(uri, position);
        } else {
            mActionsListener.onImageUnavailable();
        }
    }

    ImagesAdapter.ImageItemListener mItemListener = new ImagesAdapter.ImageItemListener() {
        @Override
        public void onImageClicked(View toolsbar) {
            mActionsListener.changeToolsbarVisibility(toolsbar, true);
        }

        @Override
        public void onBtnCWClicked(Bitmap image, int position) {
            mActionsListener.rotateImageCW(image, position);
        }

        @Override
        public void onBtnCCWClicked(Bitmap image, int position) {
            mActionsListener.rotateImageCCW(image, position);
        }

        @Override
        public void onBtnMirrorClicked(Bitmap image, int position) {
            mActionsListener.mirrorImage(image, position);
        }

        @Override
        public void onImageSrcClicked(int position) {
            mActionsListener.replaceImage(position);
        }
    };

    /**
     * Fab Listener
     */

    public FabListener fabListener = new FabListener() {
        @Override
        public void onLoadImageClicked() {
            mActionsListener.addNewImage();
        }

        @Override
        public void onSaveImageClicked() {
            mActionsListener.openSaveDialog();
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_image:
                switch (fabState) {
                    case OPEN_IMAGE:
                        fabListener.onLoadImageClicked();
                        break;
                    case SAVE_IMAGE:
                        fabListener.onSaveImageClicked();
                        break;
                    default:
                        fabListener.onLoadImageClicked();
                        break;
                }
        }
    }

    public interface FabListener {
        void onLoadImageClicked();

        void onSaveImageClicked();
    }

    public enum FabState {
        OPEN_IMAGE,
        SAVE_IMAGE
    }
}
