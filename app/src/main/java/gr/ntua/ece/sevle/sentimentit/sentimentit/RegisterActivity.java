package gr.ntua.ece.sevle.sentimentit.sentimentit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import java.util.ArrayList;

import gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData.RestAdiDispenser;
import gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData.UserData;
import gr.ntua.ece.sevle.sentimentit.sentimentit.databaseApi.SimpleApi;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.Groups;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.User;
import retrofit.Callback;
import retrofit.RetrofitError;


public class RegisterActivity extends ActionBarActivity {

    private AutoCompleteTextView musernameView;
    private EditText mPasswordView;
    private EditText passwordRepeat;
    private SimpleApi simpleApi;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(gr.ntua.ece.sevle.sentimentit.sentimentit.R.layout.activity_register);
        musernameView = (AutoCompleteTextView) findViewById(gr.ntua.ece.sevle.sentimentit.sentimentit.R.id.usernameRegister);
        mPasswordView = (EditText) findViewById(gr.ntua.ece.sevle.sentimentit.sentimentit.R.id.passwordRegister);
        passwordRepeat = (EditText) findViewById(gr.ntua.ece.sevle.sentimentit.sentimentit.R.id.passwordRepeat);
        spinner = (Spinner) findViewById(gr.ntua.ece.sevle.sentimentit.sentimentit.R.id.spinner);

        //Set up rest adapter
        simpleApi = RestAdiDispenser.getSimpleApiInstance();
        spinner.setPrompt("Select the Group you belong!");

        Bundle bundle = getIntent().getExtras();
        ArrayList<Groups> groups= bundle.getParcelableArrayList("groups");

        ArrayList<String> strings = new ArrayList<String>();
        for (Groups object : groups) {
            strings.add(object != null ? object.getGroupName() : null);
        }

        ArrayAdapter<String> adapterAr = new ArrayAdapter<String> (this,android.R.layout.simple_spinner_dropdown_item,strings);
        adapterAr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterAr);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(gr.ntua.ece.sevle.sentimentit.sentimentit.R.menu.menu_register, menu);
        return true;
    }

    public void registerActivity(View view){
        // Reset errors.
        musernameView.setError(null);
        mPasswordView.setError(null);
        passwordRepeat.setError(null);
        // Store values at the time of the login attempt.
        String username = musernameView.getText().toString().replaceAll("\\r|\\n", "");;
        String password = mPasswordView.getText().toString().replaceAll("\\r|\\n", "");;
        String repeatPassword = passwordRepeat.getText().toString().replaceAll("\\r|\\n", "");;

        String spinnerT = null;
        if ((spinner.getSelectedItem())!=null)
            spinnerT = spinner.getSelectedItem().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(gr.ntua.ece.sevle.sentimentit.sentimentit.R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(gr.ntua.ece.sevle.sentimentit.sentimentit.R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            musernameView.setError(getString(gr.ntua.ece.sevle.sentimentit.sentimentit.R.string.error_field_required));
            focusView = musernameView;
            cancel = true;
        } else if (!isusernameValid(username)) {
            musernameView.setError(getString(gr.ntua.ece.sevle.sentimentit.sentimentit.R.string.error_invalid_username));
            focusView = musernameView;
            cancel = true;
        }

        //Check for a valid repeat Password
        if (!password.equals(repeatPassword))
        {
            passwordRepeat.setError(getString(gr.ntua.ece.sevle.sentimentit.sentimentit.R.string.error_invalid_repeat));
            focusView=passwordRepeat;
            cancel=true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first form
            focusView.requestFocus();
        }
        else
        {

            Groups group = new Groups();
            group.setGroupName(spinnerT);
            final User user = new User(password,username,0,group);
            Log.i("Tag", "Request data " + new Gson().toJson(user));
            System.out.println("Registration in process, checking credentials");
            simpleApi.setUser(user,new Callback<Groups>() {

                @Override
                public void success(Groups groups, retrofit.client.Response response2) {
                    System.out.println("Credentials not found in system");
                    finish();
                    UserData data = UserData.getInstance();
                    data.setUserName(user.getUserName());
                    data.setUserPoints(user.getUserPoints());
                    data.setGroupPoints(groups.getGroupPoints());
                    data.setGroupName(groups.getGroupName());
                    Intent myIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    RegisterActivity.this.startActivity(myIntent);
                }

                @Override
                public void failure(RetrofitError error) {
                    final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
                    if (activeNetwork != null && activeNetwork.isConnected()) //device is online
                    {
                        if (error != null && error.getResponse()!=null)
                        {
                            Toast.makeText(RegisterActivity.this, "Username was found in system", Toast.LENGTH_LONG).show();
                            System.out.println("Username was found in system, try again");
                            System.out.println(error.getResponse().getStatus());
                        }
                        else {       //Server is unresponsive
                            System.out.println("Server is unresponsive");
                            Toast.makeText(RegisterActivity.this, "Server is unresponsive, please try later or report to moderators", Toast.LENGTH_LONG).show();
                        }
                    }
                    else    //device not online
                    {
                        System.out.println("Connection is slow or no connection found, enable your wifi or try later");
                        Toast.makeText(getApplicationContext(), "Connection is slow or no connection found, enable your wifi or try later", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private boolean isusernameValid(String username) {

        return (username.length()>4 && username.length()<11);
    }

    private boolean isPasswordValid(String password) {

        return (password.length() > 4 && password.length()<11);
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

    @Override public void onDestroy() {
        super.onDestroy();

    }
}
