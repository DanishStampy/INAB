package com.fyp.inab.object;

import android.os.Parcel;
import android.os.Parcelable;

public class Classroom implements Parcelable {

    private String classTitle;
    private String classDescription;
    private String teacherId;

    public Classroom() {
    }

    public Classroom(String classTitle, String classDescription, String teacherId) {
        this.classTitle = classTitle;
        this.classDescription = classDescription;
        this.teacherId = teacherId;
    }

    protected Classroom(Parcel in) {
        classTitle = in.readString();
        classDescription = in.readString();
        teacherId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(classTitle);
        dest.writeString(classDescription);
        dest.writeString(teacherId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Classroom> CREATOR = new Creator<Classroom>() {
        @Override
        public Classroom createFromParcel(Parcel in) {
            return new Classroom(in);
        }

        @Override
        public Classroom[] newArray(int size) {
            return new Classroom[size];
        }
    };

    public String getClassTitle() {
        return classTitle;
    }

    public void setClassTitle(String classTitle) {
        this.classTitle = classTitle;
    }

    public String getClassDescription() {
        return classDescription;
    }

    public void setClassDescription(String classDescription) {
        this.classDescription = classDescription;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }
}
