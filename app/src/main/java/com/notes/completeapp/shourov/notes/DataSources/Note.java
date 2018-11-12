package com.notes.completeapp.shourov.notes.DataSources;

import java.util.Date;
import java.util.UUID;

public class Note {

    private UUID mId;
    private String mTitle;
    private String mNote;
    private Date mDate;
    private boolean mIsFinished;

    public Note() {
        //You will need to return a Crime with an appropriate UUID
        this(UUID.randomUUID());
    }

    public Note(UUID id) {
        //generate id
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isFinished() {
        return mIsFinished;
    }

    public void setFinished(boolean finished) {
        mIsFinished = finished;
    }

    public String getPhotoFileName(){
        return "IMG_"+getId().toString()+".jpg";
    }
}
