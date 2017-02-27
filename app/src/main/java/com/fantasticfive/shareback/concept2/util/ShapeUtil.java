package com.fantasticfive.shareback.concept2.util;

import android.app.Activity;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import java.util.Arrays;

/**
 * Created by sagar on 25/2/17.
 */
public class ShapeUtil {
    public static ShapeDrawable circularShape(Activity activity, String string){
        ShapeDrawable circularShape = new ShapeDrawable();

        float radii[] = new float[8];
        Arrays.fill(radii, 100f);
        circularShape.setShape(new RoundRectShape(radii, null, null));
        int color = ColorChooser.basedOnString(activity, string);

        circularShape.getPaint().setColor(color);
        return circularShape;
    }
}
