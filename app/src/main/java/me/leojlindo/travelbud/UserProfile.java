package me.leojlindo.travelbud;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.leojlindo.travelbud.utils.Const;

public class UserProfile extends AppCompatActivity {

    public static ArrayList<ParseUser> uList;

    /** The user. */
    public static ParseUser user = ParseUser.getCurrentUser();

    private Toolbar mToolbar;
    TextView first;
    TextView bio;
    TextView trips;
    TextView time_leaving;
    String buddy;
    ImageView prof;
    Button msg_btn;
    ParseUser u;
    TextView header;
    ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
    ParseFile imageFile;
    ParseFile routeFile;
    Button back;
    ImageView sharedRoute;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        buddy = getIntent().getStringExtra(Const.EXTRA_DATA);
        first = (TextView) findViewById(R.id.first_tv);
        first.setText(buddy);
        header = (TextView) findViewById(R.id.buddy_tv);
        header.setText(buddy);
        bio = (TextView) findViewById(R.id.bioBody_tv);
        trips = (TextView) findViewById(R.id.trips_tv);
        prof = (ImageView) findViewById(R.id.imageView3);
        msg_btn = (Button) findViewById(R.id.message_btn);
        sharedRoute = (ImageView) findViewById(R.id.shared_image);
        time_leaving = (TextView) findViewById(R.id.time_leaving);

        userQuery.whereEqualTo("username", buddy);
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> username, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved username");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

        msg_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                notificationCall();
                Log.d("Yort", "onClick: worked but no notifs");
                try {
                    userQuery.getFirst().put("viewable", true);
                    userQuery.getFirst().saveInBackground();
                    startActivity(new Intent(UserProfile.this, Chat.class).putExtra(Const.EXTRA_DATA, userQuery.getFirst().getUsername()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                //Intent i = new Intent(UserProfile.this, Chat.class);


            }
        });

        //information getting from parse
        try {
            bio.setText(userQuery.getFirst().get("bio").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            trips.setText("Trips Completed: " + Integer.toString(userQuery.getFirst().getInt("trips")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            time_leaving.setText(userQuery.getFirst().get("timeLeaving").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        setUserImage();

    }

    public void notificationCall(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
        Intent ii = new Intent(this.getApplicationContext(), UserProfile.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        //bigText.bigText(verseurl);
        bigText.setBigContentTitle("Message Request Approved!");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Message Request Approved!");
        mBuilder.setContentText("You can now message " + buddy );
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }

    private void setUserImage(){

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", buddy);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    try {
                        imageFile = objects.get(0).getParseFile("picture");
                        Bitmap bmp = BitmapFactory.decodeStream(imageFile.getDataStream());
                        prof.setImageBitmap(bmp);

                        //adding the users shared route
                        routeFile = objects.get(0).getParseFile("route");
                        Bitmap bmp2 = BitmapFactory.decodeStream(routeFile.getDataStream());
                        sharedRoute.setImageBitmap(bmp2);
                    } catch (Exception a) {
                        a.printStackTrace();
                    }
                }
            }
        });

    }






}
