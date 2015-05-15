package gr.ntua.ece.sevle.sentimentit.sentimentit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import gr.ntua.ece.sevle.sentimentit.sentimentit.databaseApi.SimpleApi;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.Groups;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.User;
import gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData.RestApiDispenser;
import gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData.UserData;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends Activity {
    //http://stackoverflow.com/questions/22209046/fix-android-studio-login-activity-template-generated-activity
    //http://instructure.github.io/blog/2013/12/09/volley-vs-retrofit/

    // UI references.
    private AutoCompleteTextView musernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private SimpleApi simpleApi;
    private Toast toast;
    private Callback<User> cb;
    Callback <ArrayList<Groups>> cb2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(gr.ntua.ece.sevle.sentimentit.sentimentit.R.layout.activity_login);
        //Set up rest adapter

        simpleApi = RestApiDispenser.getSimpleApiInstance();
        toast = Toast.makeText( this  , "" , Toast.LENGTH_SHORT );
        // Set up the login form.
        musernameView = (AutoCompleteTextView) findViewById(gr.ntua.ece.sevle.sentimentit.sentimentit.R.id.username);

        mPasswordView = (EditText) findViewById(gr.ntua.ece.sevle.sentimentit.sentimentit.R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == gr.ntua.ece.sevle.sentimentit.sentimentit.R.id.login || id == EditorInfo.IME_NULL || id==EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Button musernameSignInButton = (Button) findViewById(gr.ntua.ece.sevle.sentimentit.sentimentit.R.id.username_sign_in_button);
        musernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(gr.ntua.ece.sevle.sentimentit.sentimentit.R.id.login_form);
        mProgressView = findViewById(gr.ntua.ece.sevle.sentimentit.sentimentit.R.id.login_progress);
    }

    public void registerActivity(View view) {
        // Do something in response to press of button
        showProgress(true);

        cb2=new Callback<ArrayList<Groups>>()
        {
            @Override
            public void success(ArrayList<Groups> groups, Response response) {
                if (groups != null) {
                    System.out.println("Sign up process, getting names of groups");
                    finish();
                    Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("groups", groups);
                    myIntent.putExtras(bundle);
                    startActivity(myIntent);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                showProgress(false);
                final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
                if (activeNetwork != null && activeNetwork.isConnected()) //device is online
                {
                    if (error != null && error.getResponse()!=null)     //Server is responsive
                        System.out.println(error.getResponse().getStatus());
                    else {       //Server is unresponsive
                        System.out.println("Server is unresponsive");
                        toast.setText("Server is unresponsive, please try later or report to moderators");
                        toast.show();
                        //Toast.makeText(LoginActivity.this, "Server is unresponsive, please try later or report to moderators", Toast.LENGTH_LONG).show();
                    }
                }
                else    //device not online
                {
                    System.out.println("Connection is slow or no connection found, enable your wifi or try later");
                    toast.setText("Connection is slow or no connection found, enable your wifi or try later");
                    toast.show();
                  //  Toast.makeText(LoginActivity.this, "Connection is slow or no connection found, enable your wifi or try later", Toast.LENGTH_LONG).show();
                }

            }
        };
        simpleApi.getGroups(cb2);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        musernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = musernameView.getText().toString().replaceAll("\\r|\\n", "");
        String password = mPasswordView.getText().toString().replaceAll("\\r|\\n", "");

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

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            cb= new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    showProgress(false);
                    System.out.println("Sign in process, checking credentials");
                    if (user!=null && user.getPassword().equals("")){
                        System.out.println("Credentials are correct");
                        finish();
                        UserData data = UserData.getInstance();

                        data.setUserName(user.getUserName());
                        data.setUserPoints(user.getUserPoints());
                        data.setGroupName(user.getGroup().getGroupName());
                        data.setGroupPoints(user.getGroup().getGroupPoints());

                        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                        LoginActivity.this.startActivity(myIntent);
                    }
                    else {
                        if (user!=null)
                        {
                            System.out.println("Credentials are incorrect");
                            mPasswordView.setError(getString(gr.ntua.ece.sevle.sentimentit.sentimentit.R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                        }
                        else
                        {
                            System.out.println("Username is incorrect");
                            musernameView.setError(getString(gr.ntua.ece.sevle.sentimentit.sentimentit.R.string.error_incorrect_username));
                            musernameView.requestFocus();
                        }

                    }
                }
                @Override
                public void failure(RetrofitError error) {
                    showProgress(false);
                    final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
                    if (activeNetwork != null && activeNetwork.isConnected()) //device is online
                    {
                        if (error != null && error.getResponse()!=null)
                            System.out.println(error.getResponse().getStatus());
                        else
                        {       //Server is unresponsive
                            System.out.println("Server is unresponsive");
                            toast.setText("Server is unresponsive, please try later or report to moderators");
                            toast.show();
                            //Toast.makeText(LoginActivity.this, "Server is unresponsive, please try later or report to moderators", Toast.LENGTH_LONG).show();
                        }
                    }
                    else    //device not online
                    {
                        System.out.println("Connection is slow or no connection found, enable your wifi or try later");
                        toast.setText("Connection is slow or no connection found, enable your wifi or try later");
                        toast.show();
                        //Toast.makeText(LoginActivity.this, "Connection is slow or no connection found, enable your wifi or try later", Toast.LENGTH_LONG).show();
                    }
                }
            };
            simpleApi.getUser(username,password,cb);
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
    }
    private boolean isusernameValid(String username) {

        return (username.length()>4 && username.length()<11);
    }

    private boolean isPasswordValid(String password) {

        return (password.length() > 4 && password.length()<11);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
