package com.notes.completeapp.shourov.notes.ListDetails;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.notes.completeapp.shourov.notes.DataSources.Note;
import com.notes.completeapp.shourov.notes.Default.DefaultActivity;
import com.notes.completeapp.shourov.notes.MainDetails.NoteFragment;
import com.notes.completeapp.shourov.notes.R;
import com.notes.completeapp.shourov.notes.ViewPagerActivity.NotePagerActivity;

import java.util.UUID;

public class NoteListActivity extends DefaultActivity implements NoteListFragment.callBacks, NoteFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return new NoteListFragment();
    }

    //for both tablet and mobile device
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onNoteSelected(Note note) {

        //if there is no notefragment then open one
        if (findViewById(R.id.details_fragment_container) == null) {
            Intent intent = NotePagerActivity.newIntent(this, note.getId());
            startActivity(intent);
        } else {//if there is one replace with new one
            Fragment newDetail = NoteFragment.newInstance(note.getId());
            getSupportFragmentManager().beginTransaction().replace(R.id.details_fragment_container, newDetail).commit();

        }

    }

    @Override
    public void onNoteUpdate(Note note) {
        //Reload list
        NoteListFragment listFragment = (NoteListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
