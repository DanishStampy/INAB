package com.fyp.inab.object;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestBook implements Parcelable {

    private String student_id;
    private String student_name;
    private String book_id;
    private String book_name;
    private String status_request;
    private String return_date;

    public RequestBook() {
    }

    public RequestBook(String student_id, String student_name, String book_id, String book_name, String status_request, String return_date) {
        this.student_id = student_id;
        this.student_name = student_name;
        this.book_id = book_id;
        this.book_name = book_name;
        this.status_request = status_request;
        this.return_date = return_date;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getStatus_request() {
        return status_request;
    }

    public void setStatus_request(String status_request) {
        this.status_request = status_request;
    }

    public String getReturn_date() {
        return return_date;
    }

    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }

    protected RequestBook(Parcel in) {
        student_id = in.readString();
        student_name = in.readString();
        book_id = in.readString();
        book_name = in.readString();
        status_request = in.readString();
        return_date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(student_id);
        dest.writeString(student_name);
        dest.writeString(book_id);
        dest.writeString(book_name);
        dest.writeString(status_request);
        dest.writeString(return_date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RequestBook> CREATOR = new Creator<RequestBook>() {
        @Override
        public RequestBook createFromParcel(Parcel in) {
            return new RequestBook(in);
        }

        @Override
        public RequestBook[] newArray(int size) {
            return new RequestBook[size];
        }
    };
}
