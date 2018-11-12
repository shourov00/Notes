package com.notes.completeapp.shourov.notes.MainDetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.notes.completeapp.shourov.notes.DataSources.Note;
import com.notes.completeapp.shourov.notes.DataSources.NoteLab;
import com.notes.completeapp.shourov.notes.Picture.PictureDialog;
import com.notes.completeapp.shourov.notes.Picture.PictureUtils;
import com.notes.completeapp.shourov.notes.R;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class NoteFragment extends Fragment {

    private static final String KEY_START_ACTIVITY = "START_ACTIVITY";
    private static final int REQUEST_PHOTO = 0;

    private Note mNote;
    private EditText mNoteTitle, mNoteNote;
    private ImageButton mNoteCamera;
    private ImageView mNotePhoto;
    private CheckBox mFinishedCheckBox;
    private Button mShareButton;
    private File mPhotoFile;
    private Callbacks mCallbacks;

    //callback interface that implements list activity for showing update result
    public interface Callbacks{
        void onNoteUpdate(Note note);
    }

    public static NoteFragment newInstance(UUID noteId) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_START_ACTIVITY, noteId);

        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID noteId = (UUID) getArguments().getSerializable(KEY_START_ACTIVITY);
        mNote = NoteLab.get(getActivity()).getNote(noteId);

        //grabbing photo location
        mPhotoFile = NoteLab.get(getActivity()).getPhotoFile(mNote);
    }

    @Override
    public void onPause() {
        super.onPause();
        //update notelab copy
        NoteLab.get(getActivity()).updateNote(mNote);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_note, container, false);

        //wire up widget
        mNoteTitle = v.findViewById(R.id.note_title);
        mNoteNote = v.findViewById(R.id.note_note);
        mNoteCamera = v.findViewById(R.id.note_camera);
        mNotePhoto = v.findViewById(R.id.note_photo);
        mFinishedCheckBox = v.findViewById(R.id.note_finished);
        mShareButton = v.findViewById(R.id.note_share);

        mNoteTitle.setText(mNote.getTitle());//get value from list
        mNoteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setTitle(s.toString());
                updateNote();//when change title
            }

            @Override
            public void afterTextChanged(Editable s) {
                //empty
            }
        });

        mNoteNote.setText(mNote.getNote());//get value from list
        mNoteNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setNote(s.toString());
                updateNote();//when change note
            }

            @Override
            public void afterTextChanged(Editable s) {
                //empty
            }
        });

        mFinishedCheckBox.setChecked(mNote.isFinished());//get value from list
        mFinishedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mNote.setFinished(isChecked);
                updateNote();//when checked changed
            }
        });

        //share note
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain").setText(getNoteReport()).getIntent();
                startActivity(shareIntent);
            }
        });

        final PackageManager packageManager = getActivity().getPackageManager();
        //taking pictures
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //there is any camera?
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mNoteCamera.setEnabled(canTakePhoto);

        mNoteCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Calling FileProvider.getUriForFile(…) translates your local filepath into a Uri the camera app can see
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.notes.completeapp.shourov.notes.fileprovider",mPhotoFile);

                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);

                // To actually write to it, though, you need to grant the camera app permission
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,packageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage,REQUEST_PHOTO);

            }
        });

        //update photo
        updatePhotoView();

        //note photo clicked
        mNotePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager manager = getFragmentManager();
                PictureDialog dialog = PictureDialog.newInstance(mPhotoFile);
                dialog.show(manager,"pictureDialog");

            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.notes.completeapp.shourov.notes.fileprovider",mPhotoFile);

            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updateNote();//when changed photo
            updatePhotoView();
        }
    }

    //update note when typing
    public void updateNote(){
        NoteLab.get(getActivity()).updateNote(mNote);
        mCallbacks.onNoteUpdate(mNote);
    }

    //efficient recycler view reloading
    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }

    private String getNoteReport() {
        String finishedString = null;
        if (mNote.isFinished()) {
            finishedString = getString(R.string.nore_report_solved);
        } else {
            finishedString = getString(R.string.note_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mNote.getDate()).toString();
        String note = mNote.getNote();

        if (note == null) {
            note = getString(R.string.note_report_no_note);
        } else {
            note = getString(R.string.note_report_note, note);
        }
        String report = getString(R.string.note_report, mNote.getTitle(), dateString, finishedString, note);
        return report;
    }

    //photo view update
    private void updatePhotoView(){

        if(mPhotoFile == null || !mPhotoFile.exists()){
            mNotePhoto.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mNotePhoto.setImageBitmap(bitmap);
        }

    }

}
