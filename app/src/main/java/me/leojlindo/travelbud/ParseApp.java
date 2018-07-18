package me.leojlindo.travelbud;

import android.app.Application;

import com.parse.Parse;

public class ParseApp extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        //ParseObject.registerSubclass(Post.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
            .applicationId("ticketAway")
            .clientKey("ticketMaster")
            .server("http://ticketaway.herokuapp.com/parse")
            .build();
        Parse.initialize(configuration);
    }

}
