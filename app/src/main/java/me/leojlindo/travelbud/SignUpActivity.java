package me.leojlindo.travelbud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    private EditText firstnameInput;
    private EditText lastnameInput;
    private EditText usernameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private EditText passwordInput;
    private Button signupBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstnameInput = findViewById(R.id.firstname_tv);
        lastnameInput = findViewById(R.id.lastname_tv);
        usernameInput = findViewById(R.id.user_tv);
        emailInput = findViewById(R.id.email_tv);
        phoneInput = findViewById(R.id.phone_tv);
        passwordInput = findViewById(R.id.password_tv);
        signupBtn = findViewById(R.id.Sign_up_btn);


        // connecting sign up button to the main page after signing up
        signupBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();
                final String email = emailInput.getText().toString();

                signUp(view);
            }
        });
    }
    // creating a new user and sending the data to the server
    private void signUp(View view){
        firstnameInput = findViewById(R.id.firstname_tv);
        lastnameInput = findViewById(R.id.lastname_tv);
        usernameInput = findViewById(R.id.user_tv);
        emailInput = findViewById(R.id.email_tv);
        phoneInput = findViewById(R.id.phone_tv);
        passwordInput = findViewById(R.id.password_tv);
        signupBtn = findViewById(R.id.Sign_up_btn);

        ParseUser user = new ParseUser();

        // filling inputs for new user
        user.setPassword(passwordInput.getText().toString());
        user.setUsername(usernameInput.getText().toString());
        user.setEmail(emailInput.getText().toString());
        user.put("firstName", firstnameInput.getText().toString());
        user.put("lastName", lastnameInput.getText().toString());
        user.put("phoneNum",phoneInput.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SignUp", "Sign Up Success");
                    final Intent intent = new Intent(SignUpActivity.this, Biography.class);
                    startActivity(intent);
                    finish();

                } else {
                    Log.d("SignUp", "Sign Up Failed");
                    Toast.makeText(SignUpActivity.this,"Sign Up Failed",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

}
