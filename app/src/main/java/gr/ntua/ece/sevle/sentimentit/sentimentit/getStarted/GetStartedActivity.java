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
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
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
import gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData.RestApiDispenser;
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
    SwipeMenuListView tweetText;
    ArrayList<String> tweetData = new ArrayList<String>();
    AppAdapter adapter;
    TextView keywordText;
    Boolean needData;
    UserData data;
    Bus bus;
    List<Data> tweets;
    TweetLoader tweetLoader;
    ProgressBar mProgressView;
    private SimpleApi simpleApi;
    int buttonId;

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tweetData.size();
        }

        @Override
        public String getItem(int position) {
            return tweetData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.custom_swipelistview, null);
                new ViewHolder(convertView);
            }
            TextView tv = (TextView)convertView.findViewById(R.id.item_label);
            Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/Caviar_Dreams_Bold.ttf");
            tv.setTypeface(tf);
            tv.setText(getItem(position));
            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;

            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.image_view);
                tv_name = (TextView) view.findViewById(R.id.item_label);
                view.setTag(this);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        tweetText = (SwipeMenuListView)findViewById(R.id.listView);
        adapter = new AppAdapter();
        tweetText.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                //create an action that will be showed on swiping an item in the list
                SwipeMenuItem item1 = new SwipeMenuItem(getApplicationContext());
                item1.setBackground(new ColorDrawable(Color.DKGRAY));
                // set width of an option (px)
                item1.setWidth(200);
                item1.setTitle("Skip");
                item1.setTitleSize(18);
                item1.setTitleColor(Color.WHITE);
                menu.addMenuItem(item1);

                SwipeMenuItem item2 = new SwipeMenuItem(getApplicationContext());
                // set item background
                item2.setBackground(new ColorDrawable(Color.RED));
                item2.setWidth(200);
                item2.setTitle("Irrelevant");
                item2.setTitleSize(18);
                item2.setTitleColor(Color.WHITE);
                menu.addMenuItem(item2);
            }
        };
        //set MenuCreator
        tweetText.setMenuCreator(creator);
        // set SwipeListener
        tweetText.setClickable(false);

        tweetText.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        tweetText.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        buttonId=0;
                        castVote(getCurrentFocus(), 0);
                        break;
                    case 1:
                        buttonId=1;
                        castVote(getCurrentFocus(), 1);
                        break;
                }
                return false;
            }});

        ImageButton button = (ImageButton)findViewById(R.id.swipeButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweetText.smoothOpenMenu(0);
            }
        });

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
        tweets=new ArrayList<Data>();

        //BUTTONS
        happyButton=(ImageButton)findViewById(R.id.happyButton);
        neutralButton=(ImageButton)findViewById(R.id.neutralButton);
        sadButton=(ImageButton)findViewById(R.id.sadButton);

        tweetText = (SwipeMenuListView)findViewById(R.id.listView);
        //PROGRESS BAR
        mProgressView= (ProgressBar)findViewById(R.id.progressBar);
        showProgress(true);

        //BUS FOR LOADER-ACTIVITY COMMUNICATION
        bus = BusProvider.getInstance();

        //LOADER
        tweetLoader = new TweetLoader(bus,data.getUserName());
        //LOCK FOR LOADALL METHOD

        needData =true;
        simpleApi = RestApiDispenser.getSimpleApiInstance();
    }



    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    protected void onResume(){
        super.onResume();
        lockButtons();
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

    public void onClick(View v) {//}, int d) {
        castVote(v, -1);
    }

    public void castVote(View v, int id){
        if (!happyButton.isClickable())
            return;
        showProgress(false);
        lockButtons();
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (id!=0) {
            //Check if phone is set to "Silent or Vibrate"
            switch (am.getRingerMode()) {
                case AudioManager.RINGER_MODE_SILENT:
                    Log.i("MyApp", "Silent mode");
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    Log.i("MyApp", "Vibrate mode");
                    break;
                case AudioManager.RINGER_MODE_NORMAL: {
                    Random random = new Random();
                    int hit = random.nextInt(2) + 1;
                    playSound(hit);
                    Log.i("MyApp", "Normal mode");
                }
                break;
            }
        }

        UserVotedData uvd = new UserVotedData();
        UserVotedDataPK uvdPK = new UserVotedDataPK();
        uvdPK.setDataID(tweets.get(0).getDataID());
        uvdPK.setUserName(data.getUserName());
        if (id==-1)
            switch(v.getId())
            {
                case R.id.happyButton:
                    buttonId=2;
                    uvd.setSos(1.0);
                    break;
                case R.id.neutralButton:
                    buttonId=3;
                    uvd.setSos(0.0);
                    break;
                case R.id.sadButton:
                    buttonId=4;
                    uvd.setSos(-1.0);
                    break;
                default:
                    break;
            }
        else if (id==1)
            uvd.setSos(-27.0);
        else if (id==0)
            uvd.setSos(-47.0);
        uvd.setUservoteddataPK(uvdPK);
        Log.i("Tag", "Request data " + new Gson().toJson(uvd));
        simpleApi.setUserVotedData(uvd, new Callback<com.squareup.okhttp.Response>()
        {
            @Override
            public void success(com.squareup.okhttp.Response response, retrofit.client.Response response2)
            {
                if (buttonId!=0) {
                    data.setGroupPoints(data.getGroupPoints() + 1);
                    data.setUserPoints(data.getUserPoints() + 10);
                    groupPointsTextView.setText(data.getGroupPoints() + "");
                    userPointsTextView.setText(data.getUserPoints() + "");
                }
                tweets.remove(0);
                if (tweets.size() != 0) {
                    SpannableString content = new SpannableString("Keyword: " + tweets.get(0).getKeyword() + "");
                    content.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, content.length(), 0);
                    keywordText.setText(content);
                    tweetData.clear();
                    tweetData.add(tweets.get(0).getTweet() + "");
                    adapter.notifyDataSetChanged();
                    showProgress(false);
                    unlockButtons();
                } else {
                    needData = true;
                    tweetLoader.loadAll();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                keywordText.setText("");
                tweetData.clear();
                tweetData.add("An error occurred during distribution of data!");
                adapter.notifyDataSetChanged();
                showProgress(false);
                lockButtons();
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
            tweets.clear();
            tweets=event.tweets;
            if (tweets.size()!=0) {
                String spaceFree = tweets.get(0).getTweet().replaceAll("\\s+", " ");
                SpannableString content = new SpannableString("Keyword: "+tweets.get(0).getKeyword()+"");
                content.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),0,content.length(), 0);
                keywordText.setText(content);
                tweetData.clear();
                tweetData.add(spaceFree);
                adapter.notifyDataSetChanged();
                needData = false;
                showProgress(false);
                unlockButtons();
            }
            else
            {
                tweetData.clear();
                tweetData.add("Congratulations, you have completed \"sentimentation\" of all tweets in our database. \nCheck back later for more!");
                adapter.notifyDataSetChanged();
                showProgress(false);
                lockButtons();
            }

        }
        else
            unlockButtons();
    }

    @Subscribe
    public void onTweetsLoadFailure(TweetsLoadFailedEvent event)
    {
        Log.i("GetStartedActivity", "FAILURE");
        tweetData.clear();
        tweetData.add("An error occurred with your connection, please try later: \n");//+event.error);
        adapter.notifyDataSetChanged();
        showProgress(false);
        lockButtons();

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
            simpleApi = RestApiDispenser.getSimpleApiInstance();
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

