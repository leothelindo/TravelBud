package me.leojlindo.travelbud;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class AccountFragment extends Fragment {

    Button signOut_btn;
    TextView firstname_tv;
    TextView lastname_tv;
    TextView phone_tv;
    TextView bio_tv;
    ImageView prof_iv;
    TextView trips_iv;


    //onCreateView method is called when Fragment should create its View object hierarchy,
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment

        return inflater.inflate(R.layout.fragment_account, parent, false);
    }

    //triggered right after onCreateView()
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        firstname_tv = (TextView) view.findViewById(R.id.first_tv);
        firstname_tv.setText(ParseUser.getCurrentUser().get("firstName").toString());
        lastname_tv = (TextView) view.findViewById(R.id.last_tv);
        lastname_tv.setText(ParseUser.getCurrentUser().get("lastName").toString());
        //phone_tv = (TextView) view.findViewById(R.id.phone_tv);
        //phone_tv.setText(ParseUser.getCurrentUser().get("phoneNum").toString());
        bio_tv = (TextView) view.findViewById(R.id.bioBody_tv);
        bio_tv.setText(ParseUser.getCurrentUser().get("bio").toString());
        trips_iv = (TextView) view.findViewById(R.id.trips_tv);
        trips_iv.setText(Integer.toString(ParseUser.getCurrentUser().getInt("trips")));
        prof_iv = (ImageView) view.findViewById(R.id.imageView3);
        ParseFile imageFile = (ParseFile) ParseUser.getCurrentUser().get("picture");
        imageFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                prof_iv.setImageBitmap(bitmap);
            }
        });



        signOut_btn = (Button) view.findViewById(R.id.signOut_btn);
        signOut_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

                Toast.makeText(getContext(),"Logged Out",  Toast.LENGTH_LONG).show();
            }
        });
    }



}
