package com.notes.completeapp.shourov.notes.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.notes.completeapp.shourov.notes.Database.NoteDbSchema.NoteTable;

public class NoteDataBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "note.db";

    public NoteDataBaseHelper( Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {

            db.execSQL("create table "+NoteTable.NAME+"("+
                    " _id integer primary key autoincrement,"+
                    NoteTable.Cols.UUID+ ", " +
                    NoteTable.Cols.DATE + ", " +
                    NoteTable.Cols.TITLE + ", " +
                    NoteTable.Cols.NOTE + ", " +
                    NoteTable.Cols.FINISHED +
                    ")"
            );

        } catch (Exception e) {
            Log.d("Exception", " : " + e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        try {
            db.execSQL("DROP TABLE IF EXISTS "+NoteTable.NAME);
            onCreate(db);

        } catch (Exception e) {
            Log.d("Exception", " : " + e);
        }

    }
}
