package com.fantasticfive.shareback.concept2.util;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.fantasticfive.shareback.R;

/**
 * Created by sagar on 25/2/17.
 */
public class ColorChooser {
    private static final int colorArray[] = {
            //R.color.icon_amber,
            R.color.icon_blue_grey,
            //R.color.icon_deep_purple,
            R.color.icon_green,
            R.color.icon_light_green,
            //R.color.icon_orange,
            //R.color.icon_pink,
            //R.color.icon_purple,
            //R.color.icon_red,
            R.color.icon_teal,
            R.color.icon_light_blue,
    };

    public static int basedOnString(Activity activity, String string){
        int i = (string.charAt(0)+string.charAt(string.length()-1))%colorArray.length;
        return ContextCompat.getColor(activity, colorArray[i]);
    }

    public static int transparent(){
        return Color.TRANSPARENT;
    }
}
