package com.fyp.inab.object;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String email;
    private String fullName;
    private String phoneNum;
    private String role;
    private String school;

    public User() {
    }

    public User(String email, String fullName, String phoneNum, String role, String school) {
        this.email = email;
        this.fullName = fullName;
        this.phoneNum = phoneNum;
        this.role = role;
        this.school = school;
    }

    protected User(Parcel in) {
        email = in.readString();
        fullName = in.readString();
        phoneNum = in.readString();
        role = in.readString();
        school = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(fullName);
        parcel.writeString(phoneNum);
        parcel.writeString(role);
        parcel.writeString(school);
    }
}
