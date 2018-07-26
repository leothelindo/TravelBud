package me.leojlindo.travelbud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class PopUp extends Activity {

    private Button friendsBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_up_window);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm. heightPixels;
        friendsBtn = findViewById(R.id.find_friends_btn);

        getWindow().setLayout((int)(width *1.2), (int) (height*.3));


        //when go button is clicked it draws the route
        friendsBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(PopUp.this, UserList.class));
            }
        });


    }
}
