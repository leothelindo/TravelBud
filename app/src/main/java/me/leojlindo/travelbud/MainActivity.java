package me.leojlindo.travelbud;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int ERROR_DIALOG_REQUEST = 9001;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    NavigationView navigationView;
    Fragment accountFragment = new AccountFragment();
    Fragment homeFragment = new HomeFragment();
    Fragment settingFragment = new SettingFragment();
    Fragment messageFragment = new MessageFragment();



    TextView firstname_tv;
    TextView lastname_tv;
    ImageView prof_iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("ticketAway")
                .clientKey("ticketMaster")
                .server("http://ticketaway.herokuapp.com/parse")
                .build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // handle navigation selection
        navigationView = (NavigationView) findViewById(R.id.navigation_toolbar);


        final FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer,homeFragment).commit();




        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.user:
                                fragmentTransaction.replace(R.id.flContainer, accountFragment).commit();
                                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
                                mDrawerLayout.closeDrawers();
                                return true;
                            case R.id.home:
                                fragmentTransaction.replace(R.id.flContainer, homeFragment).commit();
                                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
                                mDrawerLayout.closeDrawers();
                                return true;
                            case R.id.setting:
                                fragmentTransaction.replace(R.id.flContainer, settingFragment).commit();
                                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
                                mDrawerLayout.closeDrawers();
                                return true;
                            case R.id.message:
                                fragmentTransaction.replace(R.id.flContainer, messageFragment).commit();
                                mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
                                mDrawerLayout.closeDrawers();
                                return true;
                        }

                        return false;
                    }
                });

        // set the new nav bar to the action bar
        mToolbar = (Toolbar) findViewById(R.id.nav_bar);
        //mToolbar.setBackground(new ColorDrawable(Color.TRANSPARENT));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        // finding the drawer from the xml
        mDrawerLayout= (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        // sets toggle to run when clicked
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        // gives us the toolbar and the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View headerView = navigationView.getHeaderView(0);

        firstname_tv = (TextView) headerView.findViewById(R.id.user_tv);
        firstname_tv.setText(ParseUser.getCurrentUser().get("firstName").toString());

        prof_iv = (ImageView) headerView.findViewById(R.id.profilepic_iv);
        ParseFile imageFile = (ParseFile) ParseUser.getCurrentUser().get("picture");
        imageFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                prof_iv.setImageBitmap(bitmap);
            }
        });
    }



    // if we select mToggle return true. Allows us to click the nav bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
