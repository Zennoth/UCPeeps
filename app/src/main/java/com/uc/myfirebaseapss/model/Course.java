package com.uc.myfirebaseapss.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Course implements Parcelable {

    private String id;
    private String subject;
    private String day;
    private String start;
    private String end;
    private String lecturer;

    public Course(){}

    public Course(String id, String subject, String day, String start, String end, String lecturer) {
        this.id = id;
        this.subject = subject;
        this.day = day;
        this.start = start;
        this.end = end;
        this.lecturer = lecturer;
    }

    protected Course(Parcel in) {
        id = in.readString();
        subject = in.readString();
        day = in.readString();
        start = in.readString();
        end = in.readString();
        lecturer = in.readString();
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getDay() {
        return day;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getLecturer() {
        return lecturer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(subject);
        dest.writeString(day);
        dest.writeString(start);
        dest.writeString(end);
        dest.writeString(lecturer);
    }
}
