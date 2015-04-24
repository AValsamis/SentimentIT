package gr.ntua.ece.sevle.sentimentit.sentimentit.databaseApi;

/**
 * Created by Sevle on 2/16/2015.
 */


import com.squareup.okhttp.Response;
import java.util.ArrayList;
import java.util.List;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.Data;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.Groups;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.User;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.UserVotedData;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface SimpleApi
{
    @GET(Constants.URL_USERWITHPASS)
    void getUser(@Path(Constants.PATH_ID1) String username,@Path(Constants.PATH_ID2) String password, Callback <User> userCallback);

    @GET(Constants.URL_GROUPS)
    void getGroups(Callback <ArrayList<Groups>> groups);

    @GET(Constants.URL_GROUPS)
    List<Groups> getGroups();

    @POST(Constants.URL_USERS)
    void setUser(@Body User user, Callback<Groups> groups);

    @GET(Constants.URL_USERS)
    List<User> getUsers();

    @GET(Constants.URL_DATA)
    void getTweets(@Path(Constants.PATH_USER) String user,Callback<List<Data>> data);

    @POST(Constants.URL_USERVOTEDDATA)
    void setUserVotedData(@Body UserVotedData uvd, Callback<Response> cb);
}