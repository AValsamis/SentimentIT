package gr.ntua.ece.sevle.sentimentit.sentimentit.leaderboard;

/**
 * Created by Sevle on 2/25/2015.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import gr.ntua.ece.sevle.sentimentit.sentimentit.R;
import gr.ntua.ece.sevle.sentimentit.sentimentit.entities.User;


public class LeaderboardUserArray extends ArrayAdapter<User> {
    private final LayoutInflater mInflater;
    private int[] colors = new int[] { 0xff9dbcbc, 0xffe4eaef };

    public LeaderboardUserArray(Context context) {
        super(context, android.R.layout.simple_list_item_2);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void setData(List<User> data) {
        clear();
        if (data != null) {
            for (User appEntry : data) {
                add(appEntry);
            }
        }
    }

    /**
     * Populate new items in the list.
     */
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.custom_list_leaderboard, parent, false);
        } else {
            view = convertView;
        }
        User item = getItem(position);
        ((TextView)view.findViewById(R.id.tv_label)).setText(item.getUserName());
        ((TextView)view.findViewById(R.id.tv_id)).setText("" +item.getUserPoints());
        int colorPos = position % colors.length;
        view.setBackgroundColor(colors[colorPos]);
        return view;
    }
}