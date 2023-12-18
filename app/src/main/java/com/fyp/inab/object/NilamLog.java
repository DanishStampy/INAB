package com.fyp.inab.object;

public class NilamLog {

    private String nilam_id;
    private String classroom_name;
    private String student_name;
    private String status_nilam;
    private String comment;
    private String date;

    public NilamLog() {
    }

    public NilamLog(String nilam_id, String classroom_name, String student_name, String status_nilam, String comment) {
        this.nilam_id = nilam_id;
        this.classroom_name = classroom_name;
        this.student_name = student_name;
        this.status_nilam = status_nilam;
        this.comment = comment;
    }

    public String getNilam_id() {
        return nilam_id;
    }

    public void setNilam_id(String nilam_id) {
        this.nilam_id = nilam_id;
    }

    public String getClassroom_name() {
        return classroom_name;
    }

    public void setClassroom_name(String classroom_name) {
        this.classroom_name = classroom_name;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getStatus_nilam() {
        return status_nilam;
    }

    public void setStatus_nilam(String status_nilam) {
        this.status_nilam = status_nilam;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
