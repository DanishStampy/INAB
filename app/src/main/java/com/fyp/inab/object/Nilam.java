package com.fyp.inab.object;

import android.os.Parcel;
import android.os.Parcelable;

public class Nilam implements Parcelable {

    private String title;
    private int pages;
    private String author;
    private String genre;
    private String lang;
    private String type;
    private String material;
    private String summary;
    private String approval;
    private String date_approved;

    public Nilam() {
    }

    public Nilam(String title, int pages, String author, String genre, String lang, String type, String material, String summary, String approval) {
        this.title = title;
        this.pages = pages;
        this.author = author;
        this.genre = genre;
        this.lang = lang;
        this.type = type;
        this.material = material;
        this.summary = summary;
        this.approval = approval;
    }

    protected Nilam(Parcel in) {
        title = in.readString();
        pages = in.readInt();
        author = in.readString();
        genre = in.readString();
        lang = in.readString();
        type = in.readString();
        material = in.readString();
        summary = in.readString();
        approval = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(pages);
        dest.writeString(author);
        dest.writeString(genre);
        dest.writeString(lang);
        dest.writeString(type);
        dest.writeString(material);
        dest.writeString(summary);
        dest.writeString(approval);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Nilam> CREATOR = new Creator<Nilam>() {
        @Override
        public Nilam createFromParcel(Parcel in) {
            return new Nilam(in);
        }

        @Override
        public Nilam[] newArray(int size) {
            return new Nilam[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getDate_approved() {
        return date_approved;
    }

    public void setDate_approved(String date_approved) {
        this.date_approved = date_approved;
    }
}
