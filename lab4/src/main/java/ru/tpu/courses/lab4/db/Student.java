package ru.tpu.courses.lab4.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Group.class, parentColumns = "id", childColumns = "group_id", onDelete = ForeignKey.NO_ACTION),
indices = {@Index(value = "group_id")})
public class Student implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    @ColumnInfo(name = "group_id")
    public int groupId;
    @NonNull
    @ColumnInfo(name = "first_name")
    public String firstName;
    @NonNull
    @ColumnInfo(name = "second_name")
    public String secondName;
    @NonNull
    @ColumnInfo(name = "last_name")
    public String lastName;
    @Nullable
    @ColumnInfo(name = "photo_path")
    public String photoPath;


    public Student(
            @NonNull int groupId,
            @NonNull String firstName,
            @NonNull String secondName,
            @NonNull String lastName,
            @Nullable String photoPath
    ) {
        this.groupId = groupId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.secondName = secondName;
        this.photoPath = photoPath;
    }

    protected Student(Parcel in) {
        id = in.readInt();
        groupId = in.readInt();
        firstName = in.readString();
        secondName = in.readString();
        lastName = in.readString();
        photoPath = in.readString();
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(groupId);
        dest.writeString(firstName);
        dest.writeString(secondName);
        dest.writeString(lastName);
        dest.writeString(photoPath);
    }

    @NonNull
    @Override
    public String toString() {
        return firstName + " " + secondName + " " + lastName;
    }
}
