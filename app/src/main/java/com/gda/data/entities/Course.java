package com.gda.data.entities;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.gda.data.utilities.MathUtils;

import java.util.ArrayList;

public class Course implements Parcelable {
    private String name;
    private double units;
    private boolean isWeighted;
    private double percent;
    private ArrayList<AssessmentType> assessmentTypes;

    public Course(String name, double units, boolean weighted) {
        this.name = name;
        this.units = units;
        this.isWeighted = weighted;
        percent = 0;
        assessmentTypes = new ArrayList<>();
    }

    protected Course(Parcel in) {
        name = in.readString();
        units = in.readDouble();
        isWeighted = in.readInt() != 0;
        percent = in.readDouble();
        assessmentTypes = new ArrayList<>();
        in.readTypedList(assessmentTypes, AssessmentType.CREATOR);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(units);
        dest.writeInt(isWeighted ? 1 : 0);
        dest.writeDouble(percent);
        dest.writeTypedList(assessmentTypes);
    }

    public void updatePercent(boolean weighted) {
        if (weighted) {
            percent = MathUtils.weightedCoursePercentage(assessmentTypes);
        } else {
            percent = MathUtils.unweightedCoursePercentage(assessmentTypes);
        }
    }

    public double getGrade() {
        if (percent >= 90) {
            return 4.0;
        } else if (90 > percent && percent >= 80) {
            return 3.0;
        } else if (80 > percent && percent >= 70) {
            return 2.0;
        } else if (70 > percent && percent >= 60) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    public String getLetter() {
        if (assessmentTypes.isEmpty()) {
            return "";
        } else if (percent >= 90) {
            return "A";
        } else if (90 > percent && percent >= 80) {
            return "B";
        } else if (80 > percent && percent >= 70) {
            return "C";
        } else if (70 > percent && percent >= 60) {
            return "D";
        } else {
            return "F";
        }
    }

    public int getColor() {
        if (assessmentTypes.isEmpty()) {
            return Color.argb(0, 0, 0, 0);
        } else if (percent >= 90) {
            return Color.argb(255, 142, 166, 4);
        } else if (90 > percent && percent >= 80) {
            return Color.argb(255, 251, 139, 36);
        } else if (80 > percent && percent >= 70) {
            return Color.argb(255, 255, 78, 0);
        } else {
            return Color.argb(255, 154, 3, 30);
        }
    }

    public boolean containsAssessmentType(String assessmentTypeName) {
        for (AssessmentType assessmentType : assessmentTypes) {
            if (assessmentType.getName().equalsIgnoreCase(assessmentTypeName)) {
                return true;
            }
        }
        return false;
    }

    public AssessmentType getAssessmentTypeFromName(String assessmentTypeName) {
        for (AssessmentType assessmentType : assessmentTypes) {
            if (assessmentType.getName().equalsIgnoreCase(assessmentTypeName)) {
                return assessmentType;
            }
        }
        return null;
    }

    public int getAssessmentTypeIndexFromName(String assessmentTypeName) {
        for (int i = 0; i < assessmentTypes.size(); i++) {
            if (assessmentTypes.get(i).getName().equalsIgnoreCase(assessmentTypeName)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isWeighted() {
        return isWeighted;
    }

    public String getName() {
        return name;
    }

    public double getUnits() {
        return units;
    }

    public double getPercent() {
        return percent;
    }

    public ArrayList<AssessmentType> getAssessmentTypes() {
        return assessmentTypes;
    }
}
