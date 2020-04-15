package ru.tpu.courses.lab2;

import android.os.Parcel;
import android.os.Parcelable;

public class Characteristics  implements Parcelable {
    public int Id = 0;
    public String Option = "";
    public double Score = 0.0;

    public Characteristics(int id, String option, double score) {
        Id = id;
        Option = option;
        Score = score;
    }

    private Characteristics(Parcel in) {
        Id = in.readInt();
        Option = in.readString();
        Score = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(Id);
        parcel.writeString(Option);
        parcel.writeDouble(Score);
    }

    public static final Parcelable.Creator<Characteristics> CREATOR = new Parcelable.Creator<Characteristics>() {
        public Characteristics createFromParcel(Parcel in) {
            return new Characteristics(in);
        }

        public Characteristics[] newArray(int size) {
            return new Characteristics[size];
        }
    };
}
