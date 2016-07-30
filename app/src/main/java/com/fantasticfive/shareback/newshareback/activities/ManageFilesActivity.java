package com.fantasticfive.shareback.newshareback.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.adapters.DirListAdapter;
import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;
import com.fantasticfive.shareback.newshareback.fileoperation.FileSender;
import com.fantasticfive.shareback.newshareback.helpers.DirHelper;
import com.fantasticfive.shareback.newshareback.helpers.FileOperationHelper;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;

public class ManageFilesActivity extends AppCompatActivity
        implements FileSender.Callback ,DirHelper.Callback
        ,DirListAdapter.Callback
        , FileOperationHelper.Callback {

    private final int FILE_SELECT_CODE = 1;
    Button btnOk;
    ListView lvDirs;
    DirHelper dirHelper;
    DirListAdapter adapter;
    FileOperationHelper fileOperationHelper = null;

    String selectedFile = null;

    boolean copyFlag = false;
    boolean moveFlag = false;

    final String OPERATION_MKDIR = "mkdir", OPERATION_RENAME = "rename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_files);

        init();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init(){
        lvDirs = (ListView) findViewById(R.id.list_dir);
        btnOk = (Button) findViewById(R.id.btnOk);

        dirHelper = new DirHelper(getApplicationContext(), this);
        dirHelper.getItemList("");
        fileOperationHelper = new FileOperationHelper(this);
    }

    private void upload(){

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.file_operations, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.action_cp:
                if(copyFlag){
                    copyFlag = false;
                    item.setTitle("Copy Selected");
                    copy(selectedFile, dirHelper.getCurrentDir());
                }
                else{
                    copyFlag = true;
                    Toast.makeText(ManageFilesActivity.this, "Go to Dir and Choose \"Paste Here\"", Toast.LENGTH_SHORT).show();
                    item.setTitle("Paste Here");
                }
                return true;

            case R.id.action_mv:
                if(moveFlag){
                    moveFlag = false;
                    item.setTitle("Move Selected");
                    move(selectedFile, dirHelper.getCurrentDir());
                }
                else{
                    moveFlag = true;
                    Toast.makeText(ManageFilesActivity.this, "Go to Dir and Choose \"Move Here\"", Toast.LENGTH_SHORT).show();
                    item.setTitle("Move Here");
                }
                return true;

            case R.id.action_del: deleteFiles(selectedFile); return true;

            case R.id.action_mkdir: showDialog(OPERATION_MKDIR); return true;
            case R.id.action_rm: showDialog(OPERATION_RENAME); return true;
            case R.id.action_upload: upload(); return true;

            default: return super.onOptionsItemSelected(item);
        }
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

    private void deleteFiles(String filePaths){
        fileOperationHelper.del(filePaths);
    }

    private void copy(String oldPath, String newPath){
        newPath = newPath + (new File(oldPath)).getName();
        fileOperationHelper.copy(oldPath, newPath);
    }

    private void move(String oldPath, String newPath){
        newPath = newPath + (new File(oldPath)).getName();
        fileOperationHelper.move(oldPath, newPath);
    }

    private void rename(String oldPath, String newName){

        String newPath = dirHelper.getCurrentDir() + newName;
        fileOperationHelper.rename(oldPath, newPath);
    }

    private void mkDir(String fileName){
        String filePath = dirHelper.getCurrentDir() + fileName + "/";
        fileOperationHelper.mkDir(filePath);
    }

    private boolean isValid(String filePath){

        String fileFormat = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
        for(String s: Constants.SUPPORTED_FORMATS){
            if(s.equals(fileFormat))
                return true;
        }
        return false;
    }

    @Override
    public void onFileUploaded(boolean status) {
        if (status) {
            Toast.makeText(ManageFilesActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
            dirHelper.refresh();
        }
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
        selectedFile = isChecked ? (dirHelper.getCurrentDir()+item) : null;
    }

    @Override
    public void onDirClicked(String item) {
        Toast.makeText(getApplicationContext(), "Clicked Dir: "+item, Toast.LENGTH_SHORT).show();
        dirHelper.getItemList(item);
    }

    @Override
    public void onOperationPerformed() {
        dirHelper.refresh();
    }

    public void showDialog(final String operationType){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(this);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                switch (operationType){
                    case OPERATION_MKDIR: mkDir(text); break;
                    case OPERATION_RENAME: rename(selectedFile, text); break;
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
}
