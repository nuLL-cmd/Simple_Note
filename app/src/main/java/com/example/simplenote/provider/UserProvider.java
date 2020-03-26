package com.example.simplenote.provider;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class UserProvider implements Parcelable {
    private String userName;
    private String userUser;
    private String userEmail;
    private String userUid;
    private String userSex;
    private String userUrlPhoto;

    public UserProvider(String userName, String userEmail, String userUid, String userSex, String userUrlPhoto, String userUser) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userUser = userUser;
        this.userUid = userUid;
        this.userSex = userSex;
        this.userUrlPhoto = userUrlPhoto;
    }

    public UserProvider(String userName, String userEmail, String userUid, String userSex, String userUser) {
        this.userName = userName;
        this.userUser = userUser;
        this.userEmail = userEmail;
        this.userUid = userUid;
        this.userSex = userSex;
    }

    public UserProvider() {
    }

    protected UserProvider(Parcel in) {
        userName = in.readString();
        userEmail = in.readString();
        userUid = in.readString();
        userSex = in.readString();
        userUser = in.readString();
        userUrlPhoto = in.readString();
    }

    public static final Creator<UserProvider> CREATOR = new Creator<UserProvider>() {
        @Override
        public UserProvider createFromParcel(Parcel in) {
            return new UserProvider(in);
        }

        @Override
        public UserProvider[] newArray(int size) {
            return new UserProvider[size];
        }
    };

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserUser() {
        return userUser;
    }

    public String getUserUid() {
        return userUid;
    }

    public String getUserSex() {
        return userSex;
    }

    public String getUserUrlPhoto() {
        return userUrlPhoto;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public void setUserUrlPhoto(String userUrlPhoto) {
        this.userUrlPhoto = userUrlPhoto;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("userName",userName);
        result.put("userEmail",userEmail);
        result.put("userUrlPhoto",userUrlPhoto);
        result.put("userUid",userUid);
        result.put("userUser",userUser);
        result.put("userSex",userSex);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userEmail);
        dest.writeString(userUser);
        dest.writeString(userUid);
        dest.writeString(userSex);
        dest.writeString(userUrlPhoto);
    }
}

