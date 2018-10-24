package com.joneill.sidebysideviewer.image.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.joneill.sidebysideviewer.R;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.google.common.base.Preconditions.checkNotNull;

/***
 * ImagesAdapter class
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    private List<Bitmap> mImages;
    private ImageItemListener mItemListener;
    private int orientation;

    //Uses a default layout orientation of vertical, or 1
    public ImagesAdapter(List<Bitmap> mImages, ImageItemListener mItemListener) {
        setList(mImages);
        this.mItemListener = mItemListener;
        orientation = GridLayoutManager.VERTICAL;
    }

    public ImagesAdapter(List<Bitmap> mImages, ImageItemListener mItemListener, int orientation) {
        setList(mImages);
        this.mItemListener = mItemListener;
        this.orientation = orientation;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View imageView = inflater.inflate(R.layout.item_image, parent, false);

        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Bitmap bitmap = mImages.get(position);
        viewHolder.imageView.setImageBitmap(bitmap);
        //Change the ImageView gravity so that images are always touching each other by default
        //If the image is the first in the list, or on top/left
        if (position == 0) {
            viewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_END);
        } else {
            viewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_START);
        }
    }

    public void replaceData(List<Bitmap> images) {
        setList(images);
        notifyDataSetChanged();
    }

    public void replaceItem(Bitmap replacement, int position) {
        mImages.remove(position);
        mImages.add(position, replacement);
        //mImages.set(position, replacement);
        notifyDataSetChanged();
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    private void setList(List<Bitmap> images) {
        mImages = checkNotNull(images);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public void addItem(Bitmap item) {
        mImages.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    public Bitmap getItem(int position) {
        return mImages.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PhotoViewAttacher.OnViewTapListener {
        public LinearLayout rootView;
        public PhotoView imageView;
        public View toolsbar;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = (LinearLayout) itemView;
            imageView = (PhotoView) itemView.findViewById(R.id.image_item_bitmap);
            imageView.setOnViewTapListener(this);

            toolsbar = itemView.findViewById(R.id.image_tools_bar);
            toolsbar.findViewById(R.id.btn_cw).setOnClickListener(this);
            toolsbar.findViewById(R.id.btn_ccw).setOnClickListener(this);
            toolsbar.findViewById(R.id.btn_img_src).setOnClickListener(this);
            toolsbar.findViewById(R.id.btn_mirror).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()) {
                case R.id.item_image:
                    mItemListener.onImageClicked(toolsbar);
                    break;
                case R.id.image_item_bitmap:
                    mItemListener.onImageClicked(toolsbar);
                    break;
                case R.id.btn_cw:
                    mItemListener.onBtnCWClicked(mImages.get(position), position);
                    break;
                case R.id.btn_ccw:
                    mItemListener.onBtnCCWClicked(mImages.get(position), position);
                    break;
                case R.id.btn_mirror:
                    mItemListener.onBtnMirrorClicked(mImages.get(position), position);
                    break;
                case R.id.btn_img_src:
                    mItemListener.onImageSrcClicked(position);
                    break;
                default:
                    mItemListener.onImageClicked(toolsbar);
                    break;
            }
        }

        @Override
        public void onViewTap(View view, float x, float y) {
            mItemListener.onImageClicked(toolsbar);
        }
    }

    public static interface ImageItemListener {
        void onImageClicked(View toolsbar);

        void onBtnCWClicked(Bitmap image, int position);

        void onBtnCCWClicked(Bitmap image, int position);

        void onBtnMirrorClicked(Bitmap image, int position);

        void onImageSrcClicked(int position);
    }
}