package gr.ntua.ece.sevle.sentimentit.sentimentit.getStarted;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

//import com.baoyz.swipemenulistview.SwipeMenu;
//import com.baoyz.swipemenulistview.SwipeMenuCreator;
//import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gr.ntua.ece.sevle.sentimentit.sentimentit.R;
import gr.ntua.ece.sevle.sentimentit.sentimentit.databaseApi.SimpleApi;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.Data;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.UserVotedData;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.UserVotedDataPK;
import gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData.BusProvider;
import gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData.RestAdiDispenser;
import gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData.UserData;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GetStartedActivity extends ActionBarActivity {

    int[] sounds={R.raw.mysound1, R.raw.mysound2};
    private SoundPoolHelper mSoundPoolHelper;
    int mSoundLessId;
    int mSoundMoreId;
    TextView groupNameTextView;
    TextView userNameTextView;
    TextView groupPointsTextView;
    TextView userPointsTextView;
    ImageButton sadButton;
    ImageButton neutralButton;
    ImageButton happyButton;
    TextView tweetText;
    TextView keywordText;
    Boolean needData;
    VideoView video;

    UserData data;
    Bus bus;
    List<Data> tweets;
    TweetLoader tweetLoader;
    ProgressBar mProgressView;
   // com.baoyz.swipemenulistview.SwipeMenuListView listView;
    private SimpleApi simpleApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        /////////////////////////////////////////////////
      //  listView = (com.baoyz.swipemenulistview.SwipeMenuListView )findViewById(R.id.listView);
     /*   SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width

                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width

                // set a icon
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

// set creator
        listView.setMenuCreator(creator);*/
        /////////////////////////////////////////////////////////////
        //SOUNDS
        mSoundPoolHelper = new SoundPoolHelper(1, this);
        mSoundLessId = mSoundPoolHelper.load(this, sounds[0], 1);
        mSoundMoreId = mSoundPoolHelper.load(this, sounds[1], 1);

        //SHAREDMEMORY DATA
        data= UserData.getInstance();

        //LEADERBOARD STATS
        groupNameTextView = (TextView)findViewById(R.id.GroupName);
        groupNameTextView.setText(data.getGroupName()+": ");

        userNameTextView = (TextView)findViewById(R.id.UserName);
        userNameTextView.setText(data.getUserName()+": ");

        groupPointsTextView = (TextView)findViewById(R.id.GroupPoints);
        groupPointsTextView.setText(" "+data.getGroupPoints());

        userPointsTextView = (TextView)findViewById(R.id.UserPoints);
        userPointsTextView.setText(" "+data.getUserPoints());

        //TWEET DATA
        keywordText=(TextView)findViewById(R.id.textKeyword);

        Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/Caviar_Dreams_Bold.ttf");
        tweetText=(TextView)findViewById(R.id.textData);
        tweetText.setTypeface(tf);
        tweets=new ArrayList<Data>();

        //BUTTONS
        happyButton=(ImageButton)findViewById(R.id.happyButton);
        neutralButton=(ImageButton)findViewById(R.id.neutralButton);
        sadButton=(ImageButton)findViewById(R.id.sadButton);
        lockButtons();

        //PROGRESS BAR
        mProgressView= (ProgressBar)findViewById(R.id.progressBar);
        showProgress(true);

        //BUS FOR LOADER-ACTIVITY COMMUNICATION
        bus = BusProvider.getInstance();

        //LOADER
        tweetLoader = new TweetLoader(bus,data.getUserName());

        //LOCK FOR LOADALL METHOD
        needData =true;
        tweetLoader.loadAll();

        simpleApi = RestAdiDispenser.getSimpleApiInstance();
    }



    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    protected void onResume(){
        super.onResume();
        bus.register(this);
        tweetLoader.loadAll();
    }

    @Override
    protected void onPause(){
        super.onPause();
        bus.unregister(this);
    }


        @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPoolHelper.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_started, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        showProgress(true);
        lockButtons();
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        //Check if phone is set to "Silent or Vibrate"
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                Log.i("MyApp", "Silent mode");
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Log.i("MyApp","Vibrate mode");
                break;
            case AudioManager.RINGER_MODE_NORMAL: {
                Random random = new Random();
                int hit = random.nextInt(2)+1;
                playSound(hit);
                Log.i("MyApp", "Normal mode");
            }
            break;
        }

        UserVotedData uvd = new UserVotedData();
        UserVotedDataPK uvdPK=new UserVotedDataPK();
        uvdPK.setDataID(tweets.get(0).getDataID());
        uvdPK.setUserName(data.getUserName());
        switch(v.getId())
        {
            case R.id.happyButton:
                uvd.setSos(1.0);
                break;
            case R.id.neutralButton:
                uvd.setSos(0.0);
                break;
            case R.id.sadButton:
                uvd.setSos(-1.0);
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
        uvd.setUservoteddataPK(uvdPK);
        Log.i("Tag", "Request data " + new Gson().toJson(uvd));
        simpleApi.setUserVotedData(uvd, new Callback<com.squareup.okhttp.Response>()
        {
            @Override
            public void success(com.squareup.okhttp.Response response, retrofit.client.Response response2)
            {
                data.setGroupPoints(data.getGroupPoints()+1);
                data.setUserPoints(data.getUserPoints() + 10);
                groupPointsTextView.setText(data.getGroupPoints()+"");
                userPointsTextView.setText(data.getUserPoints()+"");
                tweets.remove(0);

                if (tweets.size()!=0)
                {
                    keywordText.setText(tweets.get(0).getKeyword()+"");
                    tweetText.setText(tweets.get(0).getTweet() + "");
                    unlockButtons();
                    showProgress(false);
                }
                else
                {
                    needData=true;
                    tweetLoader.loadAll();
                }

            }

            @Override
            public void failure(RetrofitError error) {
               keywordText.setText("");
               tweetText.setText("An error occurred during distribution of data!");
               showProgress(false);
            }
        });


    }

    private void playSound(int soundId) {
        mSoundPoolHelper.play(soundId);
    }

    @Subscribe
    public void onTweetsLoaded(TweetsLoadedEvent event)
    {
        if (needData)
        {
            tweets=event.tweets;
            if (tweets.size()!=0) {
                String spaceFree = tweets.get(0).getTweet().replaceAll("\\s+", " ");
                keywordText.setText("");
                keywordText.setText(tweets.get(0).getKeyword());
                tweetText.setText("");
                tweetText.setText(spaceFree);
                needData = false;
                showProgress(false);
                unlockButtons();
            }
            else
            {
                tweetText.setText("Congratulations, you have completed \"sentimentation\" of all tweets in our database. \nCheck back later for more!" );
                showProgress(false);
                lockButtons();
            }

        }
    }

    @Subscribe
    public void onTweetsLoadFailure(TweetsLoadFailedEvent event)
    {
        Log.i("GetStartedActivity", "FAILURE");
        tweetText.setText("An error occurred with your connection, please try later: \n"+event.error);
        lockButtons();
        showProgress(false);

    }

    //onKeyDown,onKeyUp dummers to prevent bug in LG phones
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && "LGE".equalsIgnoreCase(Build.BRAND)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && "LGE".equalsIgnoreCase(Build.BRAND)) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


    public class TweetLoader {
        SimpleApi simpleApi;
        Bus bus;
        String Username;

        public TweetLoader(Bus bus, String Username){
            this.bus = bus;
            this.Username=Username;
            simpleApi = RestAdiDispenser.getSimpleApiInstance();
        }

        public void loadAll()
        {
            simpleApi.getTweets(Username,new Callback<List<Data>>() {
                @Override
                public void success(List<Data> data, Response response) {
                    bus.post(new TweetsLoadedEvent(data));
                }
                @Override
                public void failure(RetrofitError error) {
                    bus.post(new TweetsLoadFailedEvent(error));
                }
            });
        }
    }

    public class TweetsLoadedEvent
    {
        public List<Data> tweets;
        public TweetsLoadedEvent(List<Data> tweets) {
            this.tweets = tweets;
        }
    }

    public class TweetsLoadFailedEvent
    {
        public RetrofitError error;
        public TweetsLoadFailedEvent(RetrofitError error) {this.error=error;}
    }


    public void unlockButtons(){
        happyButton.setClickable(true);
        neutralButton.setClickable(true);
        sadButton.setClickable(true);
    }

    public void lockButtons(){
        happyButton.setClickable(false);
        neutralButton.setClickable(false);
        sadButton.setClickable(false);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show)
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            tweetText.setVisibility(show ? View.GONE : View.VISIBLE);
            tweetText.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tweetText.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            keywordText.setVisibility(show ? View.GONE : View.VISIBLE);
            keywordText.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    keywordText.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else
        {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tweetText.setVisibility(show ? View.GONE : View.VISIBLE);
            keywordText.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

