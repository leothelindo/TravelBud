package me.leojlindo.travelbud;

import android.app.Dialog;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
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

    /** The Firebase database */
    private FirebaseDatabase database;

    /** Firebase Authentication component */
    private FirebaseAuth firebaseAuth;


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

        //checking if services is ok
        //if (isServicesOK()) {
         //   init();
        //}

        // set the new nav bar to the action bar
        mToolbar = (Toolbar) findViewById(R.id.nav_bar);
        setSupportActionBar(mToolbar);
        // finding the drawer from the xml
        mDrawerLayout= (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        // sets toggle to run when clicked
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        // gives us the toolbar and the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View headerView = navigationView.getHeaderView(0);

        firstname_tv = (TextView) findViewById(R.id.user_tv);
        firstname_tv.setText(ParseUser.getCurrentUser().get("firstName").toString());
        lastname_tv = (TextView) findViewById(R.id.lastname_tv);
        lastname_tv.setText(ParseUser.getCurrentUser().get("lastName").toString());

        prof_iv = (ImageView) findViewById(R.id.profilepic_iv);
        ParseFile imageFile = (ParseFile) ParseUser.getCurrentUser().get("picture");
        imageFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                prof_iv.setImageBitmap(bitmap);
            }
        });
    }


    //checking the version
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this );

        if (available == ConnectionResult.SUCCESS) {
            //all good, user can make map request
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //error occurred but can be fixed
            Log.d(TAG, "isServicesOK: error occurred but can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You cant make map request", Toast.LENGTH_SHORT).show();
        }
        return false;
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
