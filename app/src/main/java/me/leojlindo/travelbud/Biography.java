package me.leojlindo.travelbud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class Biography extends AppCompatActivity {

    private EditText bio_et;
    private TextView name;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biography);
        name = (TextView) findViewById(R.id.firstname_tv);
        name.setText(ParseUser.getCurrentUser().getUsername());
        bio_et = (EditText) findViewById(R.id.bio_et);

        submit=(Button)findViewById(R.id.bioSubmit_btn);
        final ParseUser user = ParseUser.getCurrentUser();


        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                user.put("bio", bio_et.getText().toString());
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        final Intent intent = new Intent(Biography.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });

    }
}
