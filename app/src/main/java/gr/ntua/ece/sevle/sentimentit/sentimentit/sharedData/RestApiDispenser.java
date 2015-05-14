package gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import gr.ntua.ece.sevle.sentimentit.sentimentit.databaseApi.SimpleApi;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Sevle on 2/25/2015.
 */
public class RestApiDispenser {

    private static final String API_URL = "http://147.102.19.120:8080/SentimentIT1/webresources";
    private static RestAdapter restAdapterInstance = null;
    private static SimpleApi simpleApiInstance;

    public static RestAdapter getRestAdapter()
    {
        if (restAdapterInstance == null)
        {
            OkHttpClient s = new OkHttpClient();
            s.setConnectTimeout(10, TimeUnit.SECONDS);
            s.setReadTimeout(10, TimeUnit.SECONDS);
            s.setWriteTimeout(10, TimeUnit.SECONDS);
            RestAdapter.Builder builder = new RestAdapter.Builder().setEndpoint(API_URL).setClient(new OkClient(s));
            restAdapterInstance = builder.build();
        }
        return restAdapterInstance;
    }

    public static SimpleApi getSimpleApiInstance()
    {
        if(simpleApiInstance==null) {
            simpleApiInstance = getRestAdapter().create(SimpleApi.class);
        }
        return simpleApiInstance;
    }

}
