package me.leojlindo.travelbud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SignUpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        // connecting sign up button to the main page after signing up
        Button sign_up_btn = (Button) findViewById(R.id.Sign_up_btn);

        sign_up_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            }
        });
    }

}
