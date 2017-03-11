package com.gda.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AssessmentType implements Parcelable {
    private String name;
    private int gradePortion;
    private double percent;
    private ArrayList<Assessment> assessments;

    public AssessmentType(String name, int gradePortion) {
        this.name = name;
        this.gradePortion = gradePortion;
        percent = 0;
        assessments = new ArrayList<>();
    }

    protected AssessmentType(Parcel in) {
        name = in.readString();
        gradePortion = in.readInt();
        percent = in.readDouble();
        assessments = new ArrayList<>();
        in.readTypedList(assessments, Assessment.CREATOR);
    }

    public static final Creator<AssessmentType> CREATOR = new Creator<AssessmentType>() {
        @Override
        public AssessmentType createFromParcel(Parcel in) {
            return new AssessmentType(in);
        }

        @Override
        public AssessmentType[] newArray(int size) {
            return new AssessmentType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(gradePortion);
        dest.writeDouble(percent);
        dest.writeTypedList(assessments);
    }

    @Override
    public String toString() {
        return name;
    }

    public void updatePercent(double percent) {
        this.percent = percent;
    }

    public String getName() {
        return name;
    }

    public int getGradePortion() {
        return gradePortion;
    }

    public double getPercent() {
        return percent;
    }

    public ArrayList<Assessment> getAssessments() {
        return assessments;
    }
}
