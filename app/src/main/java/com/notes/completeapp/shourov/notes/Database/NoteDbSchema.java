package com.notes.completeapp.shourov.notes.Database;

public class NoteDbSchema {
    //database table name
    public static final class NoteTable{
        public static final String NAME = "notes";

        //database column name
        public static final class Cols{
            public static final String UUID = "id";
            public static final String DATE = "date";
            public static final String TITLE = "title";
            public static final String NOTE = "note";
            public static final String FINISHED = "finished";
        }
    }
}
