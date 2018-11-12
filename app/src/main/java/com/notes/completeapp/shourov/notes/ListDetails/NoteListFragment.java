package com.notes.completeapp.shourov.notes.ListDetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.notes.completeapp.shourov.notes.DataSources.Note;
import com.notes.completeapp.shourov.notes.DataSources.NoteLab;
import com.notes.completeapp.shourov.notes.MainDetails.NoteFragment;
import com.notes.completeapp.shourov.notes.R;
import com.notes.completeapp.shourov.notes.ViewPagerActivity.NotePagerActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

public class NoteListFragment extends Fragment {

    private static final String KEY_SAVING_SUBTITLE = "SAVING_SUBTITLE";
    private int clickedNotePosition;
    private static final String KEY_POSITION_ID = "POSITIONID";

    private RecyclerView mRecyclerView;
    private NoteAdapter mNoteAdapter;
    private FloatingActionButton mActionButton;
    private boolean mSubtitleVisible;
    private View mAddNoteView;
    private callBacks mCallBacks;


    // Required interface for hosting activities
    public interface callBacks{
        void onNoteSelected(Note note);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallBacks = (callBacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_note_list, container, false);

        //wire up widget
        mRecyclerView = v.findViewById(R.id.note_recycler_view);
        mActionButton = v.findViewById(R.id.floating_action_button);
        mAddNoteView = v.findViewById(R.id.add_note_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(KEY_SAVING_SUBTITLE);
            clickedNotePosition = savedInstanceState.getInt(KEY_POSITION_ID);
        }

        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = new Note();//instance create
                NoteLab.get(getActivity()).addNote(note);//note add in note lab
                updateUI();
                mCallBacks.onNoteSelected(note);//intent open new note pager
            }
        });

       /* updateUI();*/

        //delete note
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT || direction == ItemTouchHelper.LEFT) {
                    mNoteAdapter.swipeToDelete(viewHolder.getAdapterPosition());
                    updateUI();
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();//data set changed tell the list (10.9)
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SAVING_SUBTITLE,mSubtitleVisible);
        outState.putSerializable(KEY_POSITION_ID,clickedNotePosition);//save changes
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBacks = null;
    }

    //menu start from here
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_list_menu,menu);

        //recreate subtitle button. when user press its gonna be hide subtitle
        MenuItem subTitleItem = menu.findItem(R.id.show_notes_menu);
        if(mSubtitleVisible){
            subTitleItem.setTitle(R.string.hide_notes);
        }else{
            subTitleItem.setTitle(R.string.show_notes);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.show_notes_menu:
                mSubtitleVisible = !mSubtitleVisible;//subtitle changes
                getActivity().invalidateOptionsMenu();
                updateNotes();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateNotes(){
        NoteLab noteLab = NoteLab.get(getActivity());
        int notecount = noteLab.getNotes().size();//get note number from notelab
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural,notecount,notecount);

        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);//set note item as subtitle
    }

    //connect adapter and view holder
    public void updateUI(){

        NoteLab noteLab = NoteLab.get(getActivity());//get note lab reference
        List<Note> mNoteList = noteLab.getNotes();//get notelab list

        //visible if no note
        mAddNoteView.setVisibility(mNoteList.size() > 0 ? View.GONE : View.VISIBLE);

        if(mNoteAdapter == null){//if adapter null set list as it is
            mNoteAdapter = new NoteAdapter(mNoteList);//link note list with adapter
            mRecyclerView.setAdapter(mNoteAdapter);//link adapter with recycler view

        }else{//or not null update list
            mNoteAdapter.setNotes(mNoteList);
            mNoteAdapter.notifyItemChanged(clickedNotePosition);//update item changed
        }

        updateNotes();//update no of notes when back pressed
    }

    //viewHolder Class
    class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mNoteTitle,mNoteDate;
        private Note mNote;
        private ImageView mFinishedCheckBox;

        public NoteHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.single_note_item,container,false));

            mNoteTitle = itemView.findViewById(R.id.note_list_title);
            mNoteDate = itemView.findViewById(R.id.note_date);
            mFinishedCheckBox = itemView.findViewById(R.id.list_finished);

            itemView.setOnClickListener(this);
        }

        //each time new Note displayed
        public void bind(Note note){
            mNote = note;
            mNoteTitle.setText(mNote.getTitle());

            if(DateFormat.is24HourFormat(getContext())){
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d, yyyy  k:mm");
                mNoteDate.setText(sdf.format(mNote.getDate()));
            }
            else{
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d, yyyy  h:mm a");
                mNoteDate.setText(sdf.format(mNote.getDate()));
            }

            mFinishedCheckBox.setVisibility(note.isFinished() ? View.VISIBLE : View.GONE);

        }

        @Override
        public void onClick(View v) {
            clickedNotePosition = getAdapterPosition();//take position
            //start activity
            mCallBacks.onNoteSelected(mNote);
        }
    }

    //adapter class
    class NoteAdapter extends RecyclerView.Adapter<NoteHolder>{

        List<Note> mNotes;
        public NoteAdapter(List<Note> mNotes) {
            this.mNotes = mNotes;
        }

        @NonNull
        @Override
        public NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new NoteHolder(inflater,viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteHolder noteHolder, int position) {
            Note note = mNotes.get(position);
            noteHolder.bind(note);
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }

        //First, add a setNotes(List<note>) method to CrimeAdapter to swap out the crimes it displays.
        public void setNotes(List<Note> notes){
            mNotes = notes;
        }

        public void swipeToDelete(int position) {
            NoteLab noteLab = NoteLab.get(getActivity());
            Note note = mNotes.get(position);
            noteLab.deleteNote(note);
            mNoteAdapter.notifyItemRemoved(position);
            mNoteAdapter.notifyItemRangeChanged(position, noteLab.getNotes().size());
            Toast.makeText(getContext(),"Note Deleted", Toast.LENGTH_SHORT).show();
        }

    }

}
