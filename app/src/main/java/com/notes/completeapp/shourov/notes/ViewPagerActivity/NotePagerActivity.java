package com.notes.completeapp.shourov.notes.ViewPagerActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.notes.completeapp.shourov.notes.DataSources.Note;
import com.notes.completeapp.shourov.notes.DataSources.NoteLab;
import com.notes.completeapp.shourov.notes.MainDetails.NoteFragment;
import com.notes.completeapp.shourov.notes.R;

import java.util.List;
import java.util.UUID;

public class NotePagerActivity extends AppCompatActivity implements NoteFragment.Callbacks {

    private static final String KEY_NOTEID = "com.notes.completeapp.shourov.notes.MainDetails_NOTEID";

    private ViewPager mViewPager;
    private List<Note> mNoteList;


    //receive value from notelistfragment
    public static Intent newIntent(Context packageContext, UUID noteId){

        Intent intent = new Intent(packageContext,NotePagerActivity.class);
        intent.putExtra(KEY_NOTEID,noteId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pager);

        UUID noteId = (UUID) getIntent().getSerializableExtra(KEY_NOTEID);

        mViewPager = findViewById(R.id.note_pager_view);
        mNoteList = NoteLab.get(this).getNotes();
        FragmentManager manager = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(manager) {
            @Override
            public Fragment getItem(int i) {
                Note note = mNoteList.get(i);
                return NoteFragment.newInstance(note.getId());
            }

            @Override
            public int getCount() {
                return mNoteList.size();
            }
        });

        //set current item
        for (int i = 0; i < mNoteList.size(); i++) {
            if(mNoteList.get(i).getId().equals(noteId)){
                mViewPager.setCurrentItem(i);
                break;
            }

        }
    }

    @Override
    public void onNoteUpdate(Note note) {

    }
}
