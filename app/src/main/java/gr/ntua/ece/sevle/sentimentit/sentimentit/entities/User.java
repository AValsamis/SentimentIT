package gr.ntua.ece.sevle.sentimentit.sentimentit.entities;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.lang.reflect.Type;

public class User implements Parcelable,Comparable<User>
{

    @Expose
    private String password;
    @Expose
    private String userName;
    @Expose
    private int userPoints;
    @SerializedName("groupName")
    private Groups group;
    /**
     *
     * @return
     * The password
     */
    public User(){};

    public User(String password, String userName, int userPoints, Groups group) {
        this.password = password;
        this.userName = userName;
        this.userPoints = userPoints;
        this.group = group;
    }

    public Groups getGroup() {
        return group;
    }

    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     * The password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return
     * The userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @param userName
     * The userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     *
     * @return
     * The userPoints
     */
    public int getUserPoints() {
        return userPoints;
    }

    /**
     *
     * @param userPoints
     * The userPoints
     */
    public void setUserPoints(int userPoints) {
        this.userPoints = userPoints;
    }



    public void setGroup(Groups group) {
        this.group = group;
    }

    @Override
    public int compareTo(User user2){
       return user2.getUserPoints()-this.getUserPoints();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel in) {
        userName = in.readString();
        userPoints = in.readInt();

    }
    //Parcelable functions
    public User(Parcel in) {
        userName = in.readString();
        userPoints = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeInt(userPoints);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>()
    {
        public User createFromParcel(Parcel in)
        {
            return new User(in);
        }
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "LeaderboardData{" +
                "name='" + userName + '\'' +
                ", id='" + userPoints + '\'' +
                '}';
    }
    class MyDeserializer implements JsonDeserializer<User>
    {
        @Override
        public User deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException
        {
            // Get the "content" element from the parsed JSON
            JsonElement Groups = je.getAsJsonObject().get("groupName");

            // Deserialize it. You use a new instance of Gson to avoid infinite recursion
            // to this deserializer
            return new Gson().fromJson(Groups, User.class);

        }
    }
}