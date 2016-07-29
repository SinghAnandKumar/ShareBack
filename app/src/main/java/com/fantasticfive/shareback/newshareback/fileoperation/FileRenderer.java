package com.fantasticfive.shareback.newshareback.fileoperation;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;

/**
 * Created by sagar on 07-Jul-16.
 */
public class FileRenderer implements OnPageChangeListener{

    PDFView pdfView;
    PdfViewCallback callback;

    boolean fakePageChangedFlag = true;
    Activity activity;

    public FileRenderer(Activity activity, PdfViewCallback callback) {
        this.callback = callback;
        this.activity = activity;
    }

    public void renderS(String filePath, int pageNo){
        File file = new File(Constants.DIR_ROOT + filePath);

        pdfView = (PDFView) activity.findViewById(R.id.pdfview);
        PDFView.Configurator con = pdfView.fromFile(file)
                .defaultPage(pageNo)
                .showMinimap(false)
                .enableSwipe(true);
        con.onPageChange(this);
        con.load();
    }

    public void render(LinearLayout parent, String filePath, int pageNo){
        File file = new File(Constants.DIR_ROOT + filePath);
        String name = file.getName();
        View view = null;

        //Check File Type
        if(name.toLowerCase().contains(".pdf"))
            view = renderPdf(file, pageNo);
        //-- Check File Type

        if(view != null) {
            parent.removeAllViews();
            Log.e("My Tag", "Adding View");
            parent.addView(view);
        }

    }

    public void jumpTo(int pageNo){
        jumpTo(pageNo);
    }

    private View renderPdf( File file, int pageNo){
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
