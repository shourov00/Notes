package com.notes.completeapp.shourov.notes.DataSources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.notes.completeapp.shourov.notes.Database.NoteCursorWrapper;
import com.notes.completeapp.shourov.notes.Database.NoteDataBaseHelper;
import com.notes.completeapp.shourov.notes.Database.NoteDbSchema;
import com.notes.completeapp.shourov.notes.Database.NoteDbSchema.NoteTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteLab {
    private static NoteLab sNoteLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static NoteLab get(Context context) {

        if(sNoteLab == null){
            sNoteLab = new NoteLab(context);
        }

        return sNoteLab;
    }

    private NoteLab(Context context) {

        //give context and grunt permission for writable database
        mContext = context.getApplicationContext();
        mDatabase = new NoteDataBaseHelper(mContext).getWritableDatabase();


    }

    public void addNote(Note note){
        //data insert
        ContentValues contentValues = getContentValues(note);
        mDatabase.insert(NoteTable.NAME,null,contentValues);
    }

    public List<Note> getNotes(){
        List<Note> notes = new ArrayList<>();

        //query all notes
        NoteCursorWrapper cursorWrapper = queryNotes(null,null);

        try {

            cursorWrapper.moveToFirst();// you move it to the first element by calling moveToFirst(),
            while (!cursorWrapper.isAfterLast()){// until finally isAfterLast() tells you that your pointer is off the end of the data set.
                notes.add(cursorWrapper.getNote());
                cursorWrapper.moveToNext();// Each time you want to advance to a new row, you call moveToNext(),
            }

        } finally {
            cursorWrapper.close();
        }
        return notes;

    }
    //that provides a complete local filepath for images
    public File getPhotoFile(Note note){
        File fileDir = mContext.getFilesDir();
        return new File(fileDir,note.getPhotoFileName());
    }

    public void updateNote(Note note){
        String id = note.getId().toString();//for updating data need id
        ContentValues contentValues = getContentValues(note);

        mDatabase.update(NoteTable.NAME,contentValues,NoteTable.Cols.UUID + " = ?",new String[] {id});
    }

    public void deleteNote(Note note){
        String id = note.getId().toString();
        mDatabase.delete(NoteTable.NAME,NoteTable.Cols.UUID +" = ?",new String[] {id});
    }

    //read from database
    private NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs){

        Cursor cursor = mDatabase.query(NoteTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new NoteCursorWrapper(cursor);

    }

    public Note getNote(UUID id){

       NoteCursorWrapper noteCursorWrapper = queryNotes(NoteTable.Cols.UUID + " = ?",new String[] {id.toString()});

       try{
           if(noteCursorWrapper.getCount() == 0){
               return null;
           }
           noteCursorWrapper.moveToFirst();
           return noteCursorWrapper.getNote();

       }finally {
           noteCursorWrapper.close();

       }
    }

    //get data from user and write into database
    private static ContentValues getContentValues(Note note){
        ContentValues values = new ContentValues();
        values.put(NoteTable.Cols.UUID,note.getId().toString());
        values.put(NoteTable.Cols.DATE, note.getDate().getTime());
        values.put(NoteTable.Cols.TITLE,note.getTitle());
        values.put(NoteTable.Cols.NOTE,note.getNote());
        values.put(NoteTable.Cols.FINISHED,note.isFinished() ? 1 : 0);//finished or not

        return values;
    }
}
