package com.fantasticfive.shareback.newshareback.utils;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;

/**
 * Created by sagar on 07-Jul-16.
 */
public class FileRenderer implements OnPageChangeListener{

    PDFView pdfView;
    PdfViewCallback callback;

    boolean fakePageChangedFlag = true;

    public FileRenderer(PdfViewCallback callback) {
        this.callback = callback;
    }

    public void renderS(Activity activity, LinearLayout parent, String filePath, int pageNo){
        File file = new File(Constants.DIR_ROOT + filePath);

        pdfView = (PDFView) activity.findViewById(R.id.pdfview);
        PDFView.Configurator con = pdfView.fromFile(file)
                .defaultPage(pageNo)
                .showMinimap(false)
                .enableSwipe(true);
        con.onPageChange(this);
        con.load();
    }

    public void render(Activity activity, LinearLayout parent, String filePath, int pageNo){
        File file = new File(Constants.DIR_ROOT + filePath);
        String name = file.getName();
        View view = null;

        //Check File Type
        if(name.toLowerCase().contains(".pdf"))
            view = renderPdf(activity.getApplicationContext(), file, pageNo);
        //-- Check File Type

        if(view != null) {
            parent.removeAllViews();
            Log.e("My Tag", "Adding View");
            parent.addView(view);
        }

    }

    public void jumpTo(int pageNo){
        pdfView.jumpTo(pageNo);
    }

    private View renderPdf(Context context, File file, int pageNo){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pdfView = (PDFView) inflater.inflate(R.layout.innerlayout_pdf, null);
        PDFView.Configurator con = pdfView.fromFile(file)
                .defaultPage(pageNo)
                .showMinimap(false)
                .enableSwipe(true);
        con.onPageChange(this);
        con.load();
        fakePageChangedFlag = true;
        return pdfView;
    }

    @Override
    public void onPageChanged(int page, int pageCount) {

        if(!fakePageChangedFlag) {
            callback.onPageChanged(page);
        }else {
            fakePageChangedFlag = false;
        }

    }

    public interface PdfViewCallback{
        void onPageChanged(int pageNo);
    }
}
