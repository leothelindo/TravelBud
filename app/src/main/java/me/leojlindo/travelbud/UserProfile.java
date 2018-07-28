package me.leojlindo.travelbud;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import me.leojlindo.travelbud.utils.Const;

public class UserProfile extends AppCompatActivity {

    TextView first;
    TextView bio;
    TextView trips;
    String buddy;
    ParseUser user;
    ImageView prof;
    ParseQuery<ParseUser> userQuery = ParseUser.getQuery();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        buddy = getIntent().getStringExtra(Const.EXTRA_DATA);
        first = (TextView) findViewById(R.id.first_tv);
        first.setText(buddy);
        bio = (TextView) findViewById(R.id.bioBody_tv);
        trips = (TextView) findViewById(R.id.phone_tv);
        prof = (ImageView) findViewById(R.id.imageView3);
        /*ParseFile imageFile = null;
        try {
            imageFile = (ParseFile) userQuery.getFirst().get("picture");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        imageFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                prof.setImageBitmap(bitmap);
            }
        });*/
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


    }

}
