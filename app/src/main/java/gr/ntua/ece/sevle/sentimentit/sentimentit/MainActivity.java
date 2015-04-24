package gr.ntua.ece.sevle.sentimentit.sentimentit;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import gr.ntua.ece.sevle.sentimentit.sentimentit.getStarted.GetStartedActivity;
import gr.ntua.ece.sevle.sentimentit.sentimentit.leaderboard.LeaderboardActivity;
import gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData.UserData;

public class MainActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(gr.ntua.ece.sevle.sentimentit.sentimentit.R.layout.activity_main);
        TextView welcomeTextView = (TextView)findViewById(gr.ntua.ece.sevle.sentimentit.sentimentit.R.id.textView2);

        UserData data= UserData.getInstance();
        if (data!=null)
            welcomeTextView.setText("Welcome "+data.getUserName());
        else
            welcomeTextView.setText("");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(gr.ntua.ece.sevle.sentimentit.sentimentit.R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == gr.ntua.ece.sevle.sentimentit.sentimentit.R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /** Called when the user clicks the About button */
    public void aboutActivity(View view) {
        // Do something in response to press of button
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the LeaderBoard button */
    public void leadeboardActivity(View view) {
        // Do something in response to press of button
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the LeaderBoard button */
    public void getStartedActivity(View view) {
        // Do something in response to press of button
        Intent intent = new Intent(this, GetStartedActivity.class);
        startActivity(intent);
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
}
