package com.fantasticfive.shareback.newshareback.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.newshareback.Constants;
import com.fantasticfive.shareback.newshareback.adapters.DirManagerAdapter;
import com.fantasticfive.shareback.newshareback.beans.DirContentsBean;
import com.fantasticfive.shareback.newshareback.dialogs.CopyMoveDialog;
import com.fantasticfive.shareback.newshareback.fileoperation.FileSender;
import com.fantasticfive.shareback.newshareback.helpers.DirManagerHelper;
import com.fantasticfive.shareback.newshareback.helpers.FileOperationHelper;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;

public class ManageFilesActivity extends AppCompatActivity
        implements FileSender.Callback
        , DirManagerHelper.Callback
        , DirManagerAdapter.Callback
        , FileOperationHelper.Callback
        , CopyMoveDialog.CopyMoveDialogCallback{

    private final int FILE_SELECT_CODE = 1;

    ListView lvDirs;
    FloatingActionButton fabPaste;

    DirManagerHelper dirHelper;
    DirManagerAdapter adapter;
    FileOperationHelper fileOperationHelper = null;

    String selectedFile = null;

    boolean copyFlag = false;
    boolean moveFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_files);

        init();
    }

    /**
     * Initialization Code
     */
    private void init(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabPaste = (FloatingActionButton) findViewById(R.id.fabPaste);
        fabPaste.hide();

        lvDirs = (ListView) findViewById(R.id.list_dir);
//        lvDirs.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//        lvDirs.setMultiChoiceModeListener(new ListActionMode());

        dirHelper = new DirManagerHelper(getApplicationContext(), this);
        dirHelper.getItemList("");
        fileOperationHelper = new FileOperationHelper(this);
    }

    /**
     * Initiates File Uploading
     */
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
        inflater.inflate(R.menu.menu_upload_mkdir, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.action_mkdir:
                showNewFolderDialog();
                return true;

            case R.id.action_upload:
                upload();
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Receives file chooser result
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
            default: Toast.makeText(this, "Cannot Start CreatedSession: Unsupported File Type", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Multi File Operations
    private void multiDelete(ArrayList<String> filePaths){
        fileOperationHelper.del(filePaths);
    }
    //-- Multi File Operations


    //Start Single File Operations
    private void rename(String oldPath, String newPath){
        fileOperationHelper.rename(oldPath, newPath);
    }
    private void delete(String filePath){
        ArrayList<String> al = new ArrayList<>();
        al.add(filePath);
        fileOperationHelper.del(al);
    }
    private void mkDir(String fileName){
        String filePath = dirHelper.getCurrentDir() + fileName + "/";
        fileOperationHelper.mkDir(filePath);
    }
    //-- Stop Single File Operations

    /**
     * Check Supported format of applications
     * @param filePath
     * file to check
     * @return
     * If supported return true else false
     */
    private boolean isValid(String filePath){
        String fileFormat = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
        for(String s: Constants.SUPPORTED_FORMATS){
            if(s.equals(fileFormat))
                return true;
        }
        return false;
    }

    /**
     * Called When file is Uploaded
     * @param status
     */
    @Override
    public void onFileUploaded(boolean status) {
        if (status) {
            Toast.makeText(ManageFilesActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
            dirHelper.refresh();
        }
        else
            Toast.makeText(ManageFilesActivity.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when Directories and files are received
     * @param bean
     * Contents of Directory
     */
    @Override
    public void onListReceive(DirContentsBean bean) {
        adapter = new DirManagerAdapter(this, bean, this);
        lvDirs.setAdapter(adapter);
    }

    @Override
    public void onOperationPerformed(String operation, Boolean success) {
        String message = null;
        switch (operation){
            case Constants.FO_MKDIR:
                message = "Directory Create ";
                break;
            case Constants.FO_COPY:
                message = "Copy ";
                break;
            case Constants.FO_DELETE:
                message = "Delete ";
                break;
            case Constants.FO_MOVE:
                message = "Move ";
                break;
            case Constants.FO_RENAME:
                message = "Rename ";
                break;
        }
        if(message != null){
            if(success) {
                Toast.makeText(ManageFilesActivity.this, message + "Succeed...", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(ManageFilesActivity.this, message + "Failed...", Toast.LENGTH_SHORT).show();
            }
        }
        dirHelper.refresh();
    }

    private void showDeleteDialog(final String fileName){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete \""+fileName+"\" ?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delete(fileName);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showMultiDeleteDialog(final ArrayList<String> filePaths){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete "+filePaths.size()+" Files?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                multiDelete(filePaths);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Dialog to get New File Name
     * @param oldPath
     * old Path of file
     */
    private void showRenameDialog(final String oldPath){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set up the input
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_edittext_input, null);
        final EditText input = (EditText) view.findViewById(R.id.et_input);

        //Set Title
        builder.setTitle("Rename");
        input.setText(new File(oldPath).getName());

        builder.setView(view);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                String newPath = dirHelper.getCurrentDir() + newName;
                rename(oldPath, newPath);
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

    public void showCopyMoveDialog(int operation, String fileName){
        ArrayList<String> al = new ArrayList<>();
        al.add(fileName);
        CopyMoveDialog dialog = new CopyMoveDialog(this, dirHelper.getCurrentDir(), operation, al);
        dialog.show();
    }

    public void showMultiCopyMoveDialog(int operation, ArrayList<String> filePaths){
        CopyMoveDialog dialog = new CopyMoveDialog(this, dirHelper.getCurrentDir(), operation, filePaths);
        dialog.show();
    }

    /**
     * Dialog to get new folder name
     */
    private void showNewFolderDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set up the input
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_edittext_input, null);
        final EditText input = (EditText) view.findViewById(R.id.et_input);

            builder.setTitle("Create Folder");

        builder.setView(view);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                mkDir(text);
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

    @Override
    public void onListOptionSelected(String fileName, MenuItem item) {

        String filePath = dirHelper.getCurrentDir() + fileName;

        switch(item.getItemId()) {
            case R.id.action_del:
                showDeleteDialog(filePath);
                break;
            case R.id.action_cp:
                showCopyMoveDialog(CopyMoveDialog.COPY, filePath);
                break;
            case R.id.action_mv:
                showCopyMoveDialog(CopyMoveDialog.MOVE, filePath);
                break;
            case R.id.action_rm:
                showRenameDialog(filePath);
                break;

        }
    }

    @Override
    public void onDirClicked(String dirName) {
        dirHelper.getItemList(dirName);
    }

    @Override
    public void onMultiOptionSelected(ArrayList<String> fileNames, MenuItem item) {

        ArrayList<String> filePaths = new ArrayList<>();
        for(String name: fileNames){
            String path = dirHelper.getCurrentDir() + name;
            filePaths.add(path);
        }

        switch (item.getItemId()){
            case R.id.multi_copy:
                showMultiCopyMoveDialog(CopyMoveDialog.COPY, filePaths);
                break;
            case R.id.multi_move:
                showMultiCopyMoveDialog(CopyMoveDialog.MOVE, filePaths);
                break;
            case R.id.multi_delete:
                showMultiDeleteDialog(filePaths);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        dirHelper.getParentList();
    }

    @Override
    public void copyFiles(String destDir, ArrayList<String> al) {
        fileOperationHelper.copy(destDir, al);
    }

    @Override
    public void moveFiles(String destDir, ArrayList<String> al) {
        fileOperationHelper.move(destDir, al);
    }

    //Action Mode Implementation
    class CopyMoveActionMode implements ActionMode.Callback{

        MenuItem item;
        public CopyMoveActionMode(MenuItem item){
            this.item = item;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            switch (item.getItemId()){
                case R.id.action_cp:
                    mode.setTitle("Choose Copy Location");
                    fabPaste.show();
                    break;
                case R.id.action_mv:
                    mode.setTitle("Choose Move Location");
                    fabPaste.show();
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            fabPaste.hide();
        }
    }
    //-- Action Mode Implementation
}