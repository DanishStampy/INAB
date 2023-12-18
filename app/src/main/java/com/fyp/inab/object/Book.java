package com.fyp.inab.object;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    private String title;
    private String author;
    private int pages;
    private String summary;
    private boolean availability;
    private String genre;
    private String lang;
    private String type;

    public Book() {
    }

    public Book(String title, String author, int pages, String summary, boolean availability, String genre, String lang, String type) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.summary = summary;
        this.availability = availability;
        this.genre = genre;
        this.lang = lang;
        this.type = type;
    }

    protected Book(Parcel in) {
        title = in.readString();
        author = in.readString();
        pages = in.readInt();
        summary = in.readString();
        availability = in.readByte() != 0;
        genre = in.readString();
        lang = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeInt(pages);
        dest.writeString(summary);
        dest.writeByte((byte) (availability ? 1 : 0));
        dest.writeString(genre);
        dest.writeString(lang);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
