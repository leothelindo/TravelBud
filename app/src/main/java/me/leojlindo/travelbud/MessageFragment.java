package me.leojlindo.travelbud;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.leojlindo.travelbud.utils.Const;
import me.leojlindo.travelbud.utils.TouchEffect;
import me.leojlindo.travelbud.utils.Utils;


/**
 * The Class Chat is the Activity class that holds main chat screen. It shows
 * all the conversation messages between two users and also allows the user to
 * send and receive messages.
 */
public class MessageFragment extends Fragment{

    /** The Chat list. All other users besides the current logged in user */
    public static ArrayList<ParseUser> uList;

    /** The user. */
    public static ParseUser user = ParseUser.getCurrentUser();

    public View view;

    public ParseUser loader;

    ParseQuery query = ParseUser.getQuery();

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_message, parent, false);
        loadUserList();

        updateUserStatus(true);
        return view;
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        updateUserStatus(false);
    }

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    public void onResume()
    {
        super.onResume();
        loadUserList();

    }

    /**
     * Update user status.
     *
     * @param online
     *            true if user is online
     */
    private void updateUserStatus(boolean online)
    {
        user.put("online", online);
        user.saveEventually();
    }

    /**
     * Load list of users.
     */
    private void loadUserList() {

        final ProgressDialog dia = ProgressDialog.show(getContext(), null,
                getString(R.string.alert_loading));



        query.whereNotEqualTo("username", user.getUsername()).findInBackground();

        query.whereNotEqualTo("viewable", false)
                .findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> li, ParseException e) {
                        dia.dismiss();
                        if (li != null) {
                            if (li.size() == 0)
                                Toast.makeText(getContext(), R.string.msg_no_user_found,
                                        Toast.LENGTH_SHORT).show();
                            uList = new ArrayList<ParseUser>(li);
                            ListView list = (ListView) view.findViewById(R.id.list);
                            list.setAdapter(new UserAdapter());
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    startActivity(new Intent(getContext(),
                                            Chat.class).putExtra(Const.EXTRA_DATA, uList.get(i).getUsername()));
                                }
                            });
                        } else {
                            Utils.showDialog(
                                    getContext(),
                                    getString(R.string.err_users) + " " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });

    }


    /**
     * The Class UserAdapter is the adapter class for User ListView. This
     * adapter shows the user name and it's only online status for each item.
     */
    private class UserAdapter extends BaseAdapter
    {

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount()
        {
            return uList.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public ParseUser getItem(int arg0)
        {
            return uList.get(arg0);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View v, ViewGroup arg2)
        {
            if (v == null)
                v = getLayoutInflater().inflate(R.layout.chat_item, null);

            ParseUser c = getItem(pos);
            TextView lbl = (TextView) v;
            lbl.setText(c.getUsername());
            lbl.setCompoundDrawablesWithIntrinsicBounds(
                    c.getBoolean("online") ? R.drawable.ic_online
                            : R.drawable.ic_offline, 0, R.drawable.arrow, 0);

            return v;
        }

    }
    public class CustomActivity extends FragmentActivity implements View.OnClickListener
    {

        /**
         * Apply this Constant as touch listener for views to provide alpha touch
         * effect. The view must have a Non-Transparent background.
         */
        public final TouchEffect TOUCH = new TouchEffect();

        /* (non-Javadoc)
         * @see android.app.Activity#onCreate(android.os.Bundle)
         */
        @Override
        public void setContentView(int layoutResID)
        {
            super.setContentView(layoutResID);
        }

        /**
         * Sets the touch and click listener for a view with given id.
         *
         * @param id
         *            the id
         * @return the view on which listeners applied
         */
        public View setTouchNClick(int id)
        {

            View v = setClick(id);
            if (v != null)
                v.setOnTouchListener(TOUCH);
            return v;
        }

        /**
         * Sets the click listener for a view with given id.
         *
         * @param id
         *            the id
         * @return the view on which listener is applied
         */
        public View setClick(int id)
        {

            View v = findViewById(id);
            if (v != null)
                v.setOnClickListener(this);
            return v;
        }

        /* (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View v)
        {

        }
    }

}
