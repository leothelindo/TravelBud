package me.leojlindo.travelbud.model;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class User extends ParseObject{
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE= "image";
    private static final String KEY_USER = "user";
    private static final String KEY_FIRST = "firstName";
    private static final String KEY_LAST = "lastName";
    private static final String KEY_PHONE = "phoneNum";
    private static final String KEY_BIO = "bio";


    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
    public void setBio(String bio){
        put(KEY_BIO, bio);
    }
    public String getBio(){
        return getString(KEY_BIO);
    }

    public String getFirstName() { return getString(KEY_FIRST);}

    public String getLastName() { return getString(KEY_LAST);}

    public String getPhone() { return getString(KEY_PHONE);}

    public static class Query extends ParseQuery<User> {
        public Query(){
            super(User.class);
        }

        public Query getTop(){
            setLimit(20);
            return this;
        }

        public Query withUser(){
            include("user");
            return this;
        }
    }
}
