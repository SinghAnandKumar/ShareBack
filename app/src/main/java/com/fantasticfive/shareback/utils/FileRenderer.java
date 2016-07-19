package com.fantasticfive.shareback.utils;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.fantasticfive.shareback.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

/**
 * Created by sagar on 07-Jul-16.
 */
public class FileRenderer {


    public static void render(Activity activity, LinearLayout parent, File file){
        String name = file.getName();
        View view = null;
        //Check File Type
        if(name.toLowerCase().contains(".pdf"))
            view = renderPdf(activity, file);
        //-- Check File Type

        if(view != null) {
            parent.removeAllViews();
            Log.e("My Tag", "Adding View");
            parent.addView(view);
        }

    }

    private static View renderPdf(Context context, File file){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PDFView pdfView = (PDFView) inflater.inflate(R.layout.innerlayout_pdf, null);
        PDFView.Configurator con = pdfView.fromFile(file)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true);
        con.load();
        return pdfView;
    }
}
