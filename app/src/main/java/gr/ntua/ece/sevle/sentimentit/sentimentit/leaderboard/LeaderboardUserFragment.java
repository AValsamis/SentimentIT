package gr.ntua.ece.sevle.sentimentit.sentimentit.leaderboard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import gr.ntua.ece.sevle.sentimentit.sentimentit.sharedData.RestApiDispenser;
import gr.ntua.ece.sevle.sentimentit.sentimentit.databaseApi.SimpleApi;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.User;
import retrofit.RetrofitError;

/**
 * Created by Sevle on 2/25/2015.
 */

public class LeaderboardUserFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<User>> {

    private static final String ARG_SECTION_NUMBER = "section_number";
    Toast toast;
    LeaderboardUserArray mAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static LeaderboardUserFragment newInstance(int sectionNumber) {
        LeaderboardUserFragment fragment = new LeaderboardUserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        // Create an empty adapter we will use to display the loaded data.
        mAdapter = new LeaderboardUserArray(getActivity());
        setListAdapter(mAdapter);

        // Start out with a progress indicator.
        setListShown(false);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Insert desired behavior here.
        Log.i("DataListFragment", "Item clicked: " + id);
    }

    @Override
    public Loader<List<User>> onCreateLoader(int arg0, Bundle arg1) {
        System.out.println("UserFragment.onCreateLoader");

        return new DataListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<User>> arg0, List<User> data) {
        mAdapter.setData(data);
        System.out.println("UserFragment.onLoadFinished");
        toast = Toast.makeText( this.getActivity()  , "" , Toast.LENGTH_SHORT );
        final ConnectivityManager conMgr = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (data==null)
        {
            if (activeNetwork == null || !activeNetwork.isConnected()) {
                toast.setText("Connection is slow or no connection found, enable your wifi or try later");
                toast.show();
               // Toast.makeText(getActivity(), "Connection is slow or no connection found, enable your wifi or try later", Toast.LENGTH_SHORT).show();
            }
            else
            {
                toast.setText("Server is unresponsive, please try later or report to moderators");
                toast.show();
                //Toast.makeText(getActivity(), "Server is unresponsive, please try later or report to moderators", Toast.LENGTH_SHORT).show();
            }

        }
        // The list should now be shown.
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<User>> arg0) {
        mAdapter.setData(null);
    }


    public static class DataListLoader extends AsyncTaskLoader<List<User>> {

        List<User> mModels;

        public DataListLoader(Context context) {
            super(context);
        }

        @Override
        public List<User> loadInBackground() {
            System.out.println("UserFragmentAsyncTask.loadInBackground");
            SimpleApi simpleApi = RestApiDispenser.getSimpleApiInstance();
            List<User> users = null;
            try {
                users = simpleApi.getUsers();
            }catch(RetrofitError e)
            {
                return null;
            }
            if (users!=null)
                Collections.sort(users);
            return users;
        }

        /**
         * Called when there is new data to deliver to the client.  The
         * super class will take care of delivering it; the implementation
         * here just adds a little more logic.
         */
        @Override
        public void deliverResult(List<User> listOfData) {
            if (isReset()) {
                // An async query came in while the loader is stopped.  We
                // don't need the result.
                if (listOfData != null) {
                    onReleaseResources(listOfData);
                }
            }
            List<User> oldApps = listOfData;
            mModels = listOfData;

            if (isStarted()) {
                // If the Loader is currently started, we can immediately
                // deliver its results.
                super.deliverResult(listOfData);
            }

            // At this point we can release the resources associated with
            // 'oldApps' if needed; now that the new result is delivered we
            // know that it is no longer in use.
            if (oldApps != null) {
                onReleaseResources(oldApps);
            }
        }

        /**
         * Handles a request to start the Loader.
         */
        @Override
        protected void onStartLoading() {
            if (mModels != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mModels);
            }


            if (takeContentChanged() || mModels == null) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        /**
         * Handles a request to stop the Loader.
         */
        @Override
        protected void onStopLoading() {
            // Attempt to cancel the current load task if possible.
            cancelLoad();
        }

        /**
         * Handles a request to cancel a load.
         */
        @Override
        public void onCanceled(List<User> apps) {
            super.onCanceled(apps);

            // At this point we can release the resources associated with 'apps'
            // if needed.
            onReleaseResources(apps);
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override
        protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (mModels != null) {
                onReleaseResources(mModels);
                mModels = null;
            }
        }

        /**
         * Helper function to take care of releasing resources associated
         * with an actively loaded data set.
         */
        protected void onReleaseResources(List<User> apps) {
        }
    }

}
