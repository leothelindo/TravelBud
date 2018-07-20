package me.leojlindo.travelbud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseUser;

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

        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                ParseUser.getCurrentUser().put("bio", bio_et.getText().toString());
                final Intent intent = new Intent(Biography.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
