package me.leojlindo.travelbud;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import java.util.List;

import me.leojlindo.travelbud.utils.Const;

public class UserProfile extends AppCompatActivity {

    private Toolbar mToolbar;
    TextView first;
    TextView bio;
    TextView trips;
    String buddy;
    ParseUser user;
    ImageView prof;
    Button msg_btn;
    ParseUser u;
    TextView header;
    ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
    ParseFile imageFile;
    Button back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        back = (Button) findViewById(R.id.back);
        buddy = getIntent().getStringExtra(Const.EXTRA_DATA);
        first = (TextView) findViewById(R.id.first_tv);
        first.setText(buddy);
        header = (TextView) findViewById(R.id.buddy_tv);
        header.setText(buddy);
        bio = (TextView) findViewById(R.id.bioBody_tv);
        trips = (TextView) findViewById(R.id.phone_tv);
        prof = (ImageView) findViewById(R.id.imageView3);
        msg_btn = (Button) findViewById(R.id.message_btn);



        back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent(UserProfile.this, UserList.class);
                startActivity(i);
            }
        });

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


                try {
                    u = userQuery.getFirst();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                u.put("viewable", true);
                u.saveEventually();

                //Intent i = new Intent(UserProfile.this, Chat.class);


            }
        });

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

        //bio.setText(userQuery.get("bio").

        //trips.setText();
        //trips.setText(Integer.toString(ParseUser.getQuery().whereEqualTo("username", buddy).getInt("trips")));

        setUserImage();

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
                    } catch (Exception a) {
                        a.printStackTrace();
                    }
                }
            }
        });

    }

}
