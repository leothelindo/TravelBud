package me.leojlindo.travelbud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.leojlindo.travelbud.utils.Const;
import me.leojlindo.travelbud.utils.TouchEffect;
import me.leojlindo.travelbud.utils.Utils;

public class UserList extends AppCompatActivity implements View.OnClickListener{

    /** The Chat list. All other users besides the current logged in user */
    public static ArrayList<ParseUser> uList;

    /** The user. */
    public static ParseUser user = ParseUser.getCurrentUser();

    Context context = this;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Fragment accountFragment = new AccountFragment();
    Fragment homeFragment = new HomeFragment();
    Fragment settingFragment = new SettingFragment();
    Fragment messageFragment = new MessageFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        loadUserList();

    }

    private void loadUserList() {

        final ProgressDialog dia = ProgressDialog.show(this, null,
                getString(R.string.alert_loading));

        ParseUser.getQuery().whereNotEqualTo("username", user.getUsername())
                .findInBackground(new FindCallback<ParseUser>() {

                    @Override
                    public void done(List<ParseUser> li, ParseException e) {
                        dia.dismiss();
                        if (li != null) {
                            if (li.size() == 0)
                                Toast.makeText(context, R.string.msg_no_user_found,
                                        Toast.LENGTH_SHORT).show();

                            uList = new ArrayList<ParseUser>(li);
                            ListView list = (ListView) findViewById(R.id.user_list);
                            list.setAdapter(new UserList.UserAdapter());
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    startActivity(new Intent(context,
                                            UserProfile.class).putExtra(Const.EXTRA_DATA, uList.get(i).getUsername()));
                                }
                            });
                        } else {
                            Utils.showDialog(
                                    context,
                                    getString(R.string.err_users) + " " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });

    }



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

        // PROBABLY DONT WANT THIS BUT WE SHALL SEE
        @Override
        public View getView(int pos, View v, ViewGroup arg2)
        {
            if (v == null)
                v = getLayoutInflater().inflate(R.layout.users, null);

            ParseUser c = getItem(pos);
            TextView num = (TextView) v.findViewById(R.id.num_tv);
            num.setText("Completed Trips: " + c.get("trips").toString());
            TextView dist = (TextView) v.findViewById(R.id.shared_tv);
            dist.setText("Distance Shared: " + " miles");
            TextView lbl = (TextView) v.findViewById(R.id.big_tv);
            lbl.setText(c.getUsername());
            /*lbl.setCompoundDrawablesWithIntrinsicBounds(
                    c.getBoolean("online") ? R.drawable.ic_online
                            : R.drawable.ic_offline, 0, R.drawable.arrow, 0);*/

            return v;
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }

}
