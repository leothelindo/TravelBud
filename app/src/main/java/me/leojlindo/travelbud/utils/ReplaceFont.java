package me.leojlindo.travelbud.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

public class ReplaceFont {

    public static void replaceDefaultFont(Context context, String nameOfFontReplaced, String nameOfFont){
        Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), nameOfFont);
        replaceFont(nameOfFontReplaced, customFontTypeface);
    }

    private static void replaceFont(String nameOfFontReplaced, Typeface customFontTypeface) {
        try {
            Field myfield = Typeface.class.getDeclaredField(nameOfFontReplaced);
            myfield.setAccessible(true);
            myfield.set(null, customFontTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
}
