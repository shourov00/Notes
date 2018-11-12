package com.notes.completeapp.shourov.notes.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.notes.completeapp.shourov.notes.DataSources.Note;
import com.notes.completeapp.shourov.notes.Database.NoteDbSchema.NoteTable;

import java.util.Date;
import java.util.UUID;

//every time i pull data from cursor i need to write all of this code thats why create separate class
public class NoteCursorWrapper extends CursorWrapper {

    public NoteCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    //Add a getCrime() method that pulls out relevant column data
    public Note getNote(){

        String id = getString(getColumnIndex(NoteTable.Cols.UUID));
        long date = getLong(getColumnIndex(NoteTable.Cols.DATE));
        String title = getString(getColumnIndex(NoteTable.Cols.TITLE));
        String note = getString(getColumnIndex(NoteTable.Cols.NOTE));
        int finished = getInt(getColumnIndex(NoteTable.Cols.FINISHED));

        Note notes = new Note(UUID.fromString(id));
        notes.setDate(new Date(date));
        notes.setTitle(title);
        notes.setNote(note);
        notes.setFinished(finished != 0);

        return notes;

    }
}
