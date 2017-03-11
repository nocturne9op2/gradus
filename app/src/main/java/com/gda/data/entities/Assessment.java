package com.gda.data.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Assessment implements Parcelable {
    private String name;
    private double score;
    private int total;

    public Assessment(String name, double score, int total) {
        this.name = name;
        this.score = score;
        this.total = total;
    }

    protected Assessment(Parcel in) {
        name = in.readString();
        score = in.readDouble();
        total = in.readInt();
    }

    public static final Creator<Assessment> CREATOR = new Creator<Assessment>() {
        @Override
        public Assessment createFromParcel(Parcel in) {
            return new Assessment(in);
        }

        @Override
        public Assessment[] newArray(int size) {
            return new Assessment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(score);
        dest.writeInt(total);
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }

    public int getTotal() {
        return total;
    }
}
