package gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData;

/**
 * Created by Sevle on 2/17/2015.
 */
public class UserData {

    private static UserData instance = null;

    public static UserData getInstance() {
        if(instance == null) {
            instance = new UserData();
        }
        return instance;
    }

    String userName;
    String groupName;
    int userPoints;
    int groupPoints;

    public int getGroupPoints() {
        return groupPoints;
    }

    public void setGroupPoints(int groupPoints) {
        this.groupPoints = groupPoints;
    }

    public String getUserName() {
        return userName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getUserPoints() {
        return userPoints;
    }

    public void setUserPoints(int userPoints) {
        this.userPoints = userPoints;
    }
}
