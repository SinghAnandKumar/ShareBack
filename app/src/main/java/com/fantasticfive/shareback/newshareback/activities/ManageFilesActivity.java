package com.fantasticfive.shareback.newshareback.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.adapters.DirListAdapter;
import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;
import com.fantasticfive.shareback.newshareback.fileoperation.FileSender;
import com.fantasticfive.shareback.newshareback.helpers.DirHelper;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.util.HashSet;
import java.util.List;

public class ManageFilesActivity extends AppCompatActivity
        implements FileSender.Callback ,DirHelper.Callback
        ,DirListAdapter.Callback {

    private final int FILE_SELECT_CODE = 1;
    Button bChoose;
    Button btnOk;
    ListView lvDirs;
    DirHelper dirHelper;
    DirListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        init();

        bChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show FileChooser
                Intent intent = new Intent(ManageFilesActivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
                intent.setType("mime/pdf");

                intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
                startActivityForResult(intent, FILE_SELECT_CODE);
                //-- Show FileChooser
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init(){
        bChoose = (Button) findViewById(R.id.btnUpload);
        lvDirs = (ListView) findViewById(R.id.list_dir);
        btnOk = (Button) findViewById(R.id.btnOk);

        dirHelper = new DirHelper(getApplicationContext(), this);
        dirHelper.getItemList("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String filePath = uri.getPath();
                    if(isValid(filePath))
                        (new FileSender(ManageFilesActivity.this))
                                .execute(filePath, dirHelper.getCurrentDir()); //Test Line
                    else
                        Toast.makeText(ManageFilesActivity.this, "File Format Not Supported", Toast.LENGTH_SHORT).show();
                }
                break;
            default: Toast.makeText(this, "Cannot Start Session: Unsupported File Type", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isValid(String filePath){
        File f = new File(filePath);
        String fileFormat = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
        for(String s: Constants.SUPPORTED_FORMATS){
            if(s.equals(fileFormat))
                return true;
        }
        return false;
    }

    @Override
    public void onFileUploaded(boolean status) {
        if (status)
            Toast.makeText(ManageFilesActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(ManageFilesActivity.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListReceive(DirContentsBean bean) {
        adapter = new DirListAdapter(getApplicationContext(), bean, this);
        lvDirs.setAdapter(adapter);
    }

    @Override
    public void onFileClicked(String item, boolean isChecked) {

    }

    @Override
    public void onDirClicked(String item) {
        Toast.makeText(getApplicationContext(), "Clicked Dir: "+item, Toast.LENGTH_SHORT).show();
        dirHelper.getItemList(item);
    }
}
