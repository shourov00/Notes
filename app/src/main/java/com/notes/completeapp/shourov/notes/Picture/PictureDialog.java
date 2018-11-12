package com.notes.completeapp.shourov.notes.Picture;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.notes.completeapp.shourov.notes.R;

import java.io.File;

public class PictureDialog extends DialogFragment {

    private static final String KEY_PHOTO = "PHOTO";

    private ImageView mImageView;
    private File mPhotoFile;

    public static PictureDialog newInstance(File photofile) {

        Bundle args = new Bundle();
        args.putSerializable(KEY_PHOTO,photofile);
        PictureDialog fragment = new PictureDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        mPhotoFile = (File) getArguments().getSerializable(KEY_PHOTO);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.picture_dialog,null);
        //wire up widget
        mImageView = v.findViewById(R.id.imageView);

        Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
        mImageView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity(),R.style.ThemeOverlay_AppCompat_Dialog).setView(v).create();
    }
}
