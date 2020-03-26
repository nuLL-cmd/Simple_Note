package com.example.simplenote.provider;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class CommitmentProvider implements Parcelable {
    String title;
    String description;
    String  date;
    String commitUid;
    String status;

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("title",title);
        result.put("description", description);
        result.put("commitUid",commitUid);
        result.put("date",date);
        result.put("status", status);

        return result;
    }

    public CommitmentProvider() {
    }

    public CommitmentProvider(String title, String description, String date, String status,String commitUid) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.commitUid = commitUid;
        this.status = status;
    }

    protected CommitmentProvider(Parcel in) {
        title = in.readString();
        description = in.readString();
        date = in.readString();
        commitUid = in.readString();
        status = in.readString();
    }

    public static final Creator<CommitmentProvider> CREATOR = new Creator<CommitmentProvider>() {
        @Override
        public CommitmentProvider createFromParcel(Parcel in) {
            return new CommitmentProvider(in);
        }

        @Override
        public CommitmentProvider[] newArray(int size) {
            return new CommitmentProvider[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getCommitUid() {
        return commitUid;
    }

    public void setCommitUid(String commitUid) {
        this.commitUid = commitUid;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(commitUid);
        dest.writeString(status);
    }
}
