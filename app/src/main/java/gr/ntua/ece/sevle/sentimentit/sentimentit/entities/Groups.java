package gr.ntua.ece.sevle.sentimentit.sentimentit.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by Sevle on 2/24/2015.
 */
public class Groups implements Parcelable, Comparable<Groups>{
    @Expose
    private String groupName;
    @Expose
    private int groupPoints;

    /**
     *
     * @return
     * The groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     *
     * @param groupName
     * The groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     *
     * @return
     * The groupPoints
     */
    public int getGroupPoints() {
        return groupPoints;
    }

    /**
     *
     * @param groupPoints
     * The groupPoints
     */
    public void setGroupPoints(int groupPoints) {
        this.groupPoints = groupPoints;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel in) {
        groupName = in.readString();
        groupPoints = in.readInt();

    }
    //Parcelable functions
    public Groups(Parcel in) {
        groupName = in.readString();
        groupPoints = in.readInt();
    }
    public Groups(){}

    public Groups(String groupName, int groupPoints) {
        this.groupName = groupName;
        this.groupPoints = groupPoints;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupName);
        dest.writeInt(groupPoints);
    }

    public static final Parcelable.Creator<Groups> CREATOR = new Parcelable.Creator<Groups>()
    {
        public Groups createFromParcel(Parcel in)
        {
            return new Groups(in);
        }
        public Groups[] newArray(int size)
        {
            return new Groups[size];
        }
    };
  
    @Override
    public int compareTo(Groups another) {
        return another.getGroupPoints()-this.getGroupPoints();
    }

    @Override
    public String toString() {
        return "LeaderboardData{" +
                "name='" + groupName + '\'' +
                ", id='" + groupPoints + '\'' +
                '}';
    }
}
