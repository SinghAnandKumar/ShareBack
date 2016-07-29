package com.fantasticfive.shareback;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fantasticfive.shareback.newshareback.fileoperation.FileSender;
import com.nononsenseapps.filepicker.FilePickerActivity;

public class FileUploadActivity extends AppCompatActivity implements FileSender.Callback {

    private final int FILE_SELECT_CODE = 1;
    Button bChoose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        init();

        bChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show FileChooser
                Intent intent = new Intent(FileUploadActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                intent.setType("mime/pdf");

                intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                startActivityForResult(intent, FILE_SELECT_CODE);
                //-- Show FileChooser
            }
        });
    }

    private void init(){
        bChoose = (Button) findViewById(R.id.btnUpload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String filePath = uri.getPath();

                    (new FileSender(FileUploadActivity.this)).execute(filePath, ""); //Test Line
                }
                break;
            default: Toast.makeText(this, "Cannot Start Session: Unsupported File Type", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFileUploaded(boolean status) {
        if (status)
            Toast.makeText(FileUploadActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(FileUploadActivity.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
    }
}
