package com.fantasticfive.shareback.newshareback.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.adapters.SnackbarThumbnailAdapter;
import com.fantasticfive.shareback.beans.ThumbnailItem;
import com.fantasticfive.shareback.utils.EventReciever;
import com.fantasticfive.shareback.utils.FileRenderer;
import com.github.barteksc.pdfviewer.PDFView;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FileViewClient extends AppCompatActivity {

    LinearLayout scrollView = null;
    ImageButton addFileButton = null;
    LinearLayout container = null;

    SnackbarThumbnailAdapter horizontalAdapter = null;
    ArrayList<ThumbnailItem> thumbnailArray = new ArrayList<>();
    PDFView.Configurator con;
    EventReciever eventRecv=null;
    PDFView pdfView = null;

    private final int FILE_SELECT_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_view_client);

        init();
    }



    void init(){
        scrollView = (LinearLayout) findViewById(R.id.horizontalScrollview);
        addFileButton = (ImageButton) findViewById(R.id.add_file);
        addFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        container = (LinearLayout) findViewById(R.id.fullscreen_content);
        //testHorizScroller();
    }

    public void addFileThumbnail( final ThumbnailItem item){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.inner_snackbar_item, null);

        ImageView ivThumbnail = (ImageView) view.findViewById(R.id.inner_snackbar_thumbnail);
        ivThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileRenderer.render(FileViewClient.this, container, new File(item.getFilePath()));
            }
        });


        TextView tvFileName = (TextView) view.findViewById(R.id.inner_snackbar_filename);
        if(item.getThumbnail() != null){
            ivThumbnail.setImageDrawable(item.getThumbnail());
        }
        tvFileName.setText(item.getFileName());

        scrollView.addView(view);
    }


    public void testHorizScroller(){
        for(int i=0; i<5; i++){
            ThumbnailItem item = new ThumbnailItem();
            item.setFileName("Item "+i);
            item.setThumbnail(ResourcesCompat.getDrawable(getResources(), R.drawable.logo_pdf, null));
            addFileThumbnail(item);
        }
    }

    public void showFileChooser(){
        //Show FileChooser
        Intent intent = new Intent(FileViewClient.this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(intent, FILE_SELECT_CODE);
        //-- Show FileChooser
    }

    //On File Selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String filePath = uri.getPath();

                    ThumbnailItem item = new ThumbnailItem();
                    item.setFileName((new File(filePath)).getName());
                    item.setFilePath(filePath);

                    addFileThumbnail(item);

                    //Open File
                    FileRenderer.render(this, container, new File(filePath));
                    //-- Open File
                }
                break;
            default: Toast.makeText(this, "Cannot Start Session: Unsupported File Type", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //-- On File Selected
}
