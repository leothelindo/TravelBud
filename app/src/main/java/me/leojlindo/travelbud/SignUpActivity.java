package me.leojlindo.travelbud;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private EditText firstnameInput;
    private EditText lastnameInput;
    private EditText usernameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private EditText passwordInput;
    private Button signupBtn;
    private Button photoBtn;
    private Uri mediaUrl;
    private ImageView profInput;
    File picture;
    public static final int TAKE_PIC_REQUEST_CODE = 0;
    public static final int CHOOSE_PIC_REQUEST_CODE = 1;
    public static final int MEDIA_TYPE_IMAGE = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstnameInput = findViewById(R.id.firstname_tv);
        lastnameInput = findViewById(R.id.lastname_tv);
        usernameInput = findViewById(R.id.user_tv);
        emailInput = findViewById(R.id.email_tv);
        phoneInput = findViewById(R.id.trips_tv);
        passwordInput = findViewById(R.id.password_tv);
        signupBtn = findViewById(R.id.Sign_up_btn);
        photoBtn = findViewById(R.id.photo_btn);
        profInput = findViewById(R.id.profpic_iv);

        photoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // showing dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setTitle("Upload or Take a photo");
                builder.setPositiveButton("Upload", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        // upload image
                        Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        choosePictureIntent.setType("image/+");
                        startActivityForResult(choosePictureIntent, CHOOSE_PIC_REQUEST_CODE);
                    }
                });/*
                builder.setNegativeButton("Take Photo", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // take a photo
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mediaUrl = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        if(mediaUrl == null){
                            // displays an error
                            Toast.makeText(getApplicationContext(), "Sorry there was an error taking the picture",
                                    Toast.LENGTH_LONG).show();
                        } else{
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, mediaUrl);
                            startActivityForResult(takePicture, TAKE_PIC_REQUEST_CODE);
                        }
                    }
                });*/
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });



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
        phoneInput = findViewById(R.id.trips_tv);
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
        user.put("trips", 0);
        //user.put("picture", image/+);



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

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == CHOOSE_PIC_REQUEST_CODE){
                if(data == null) {
                    Toast.makeText(getApplicationContext(), "Image cannot be null!", Toast.LENGTH_LONG).show();
                }
                else{
                    mediaUrl = data.getData();
                    profInput.setImageURI(mediaUrl);
                }
            }
        }
    }




    private Uri getOutputMediaFileUri(int mediaTypeImage){
        if(isExternalStorageAvailable()){
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "UPLOADIMAGES");
            if(!mediaStorageDir.exists()){
                if(! mediaStorageDir.mkdirs()){
                    return null;
                }
            }
            File mediaFile = null;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            if(mediaTypeImage == MEDIA_TYPE_IMAGE){
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
            }

            Log.d("UPLOADIMAGE", "FILE: " + Uri.fromFile(mediaFile));
            return Uri.fromFile(mediaFile);
        } else{
            return null;
        }

    }

    private boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }

}
