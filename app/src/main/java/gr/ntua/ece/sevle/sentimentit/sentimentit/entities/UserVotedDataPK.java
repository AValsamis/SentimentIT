package gr.ntua.ece.sevle.sentimentit.sentimentit.entities;

/**
 * Created by Sevle on 4/1/2015.
 */

import com.google.gson.annotations.Expose;
import java.util.HashMap;
import java.util.Map;

public class UserVotedDataPK {

    @Expose
    private int dataID;

    @Expose
    private String userName;

    @Expose
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The dataID
     */
    public int getDataID() {
        return dataID;
    }

    /**
     *
     * @param dataID
     * The dataID
     */
    public void setDataID(int dataID) {
        this.dataID = dataID;
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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}