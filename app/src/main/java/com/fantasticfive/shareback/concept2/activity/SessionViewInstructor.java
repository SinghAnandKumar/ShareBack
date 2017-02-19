package com.fantasticfive.shareback.concept2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.Constants;
import com.fantasticfive.shareback.concept2.bean.SharedFile;
import com.fantasticfive.shareback.concept2.bean.User;
import com.fantasticfive.shareback.concept2.helper.FirebaseInstructorHelper;
import com.fantasticfive.shareback.concept2.view.adapters.JoinedUsersAdapter;
import com.fantasticfive.shareback.concept2.view.adapters.ShareFilesAdapter;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sagar on 16/2/17.
 */
public class SessionViewInstructor extends AppCompatActivity {

    final String TAG = "MY TAH";
    ListViewCompat lv;
    AppCompatButton btnShare;
    final int FILE_SELECT_CODE = 123;

    ArrayList<SharedFile> sharedFiles;
    ShareFilesAdapter adapter;
    FirebaseInstructorHelper firebaseHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c2_activity_session_view_instructor);
        init();
        addListeners();
        //testJoinedUserAdapter();
        //testSharedFilesAdapter();

        sharedFiles = new ArrayList<>();
        adapter = new ShareFilesAdapter(this, sharedFiles);
        lv.setAdapter(adapter);
    }

    protected void init(){
        lv = (ListViewCompat) findViewById(R.id.lv);
        btnShare = (AppCompatButton) findViewById(R.id.btnShare);

        String sessionId = getIntent().getStringExtra(Constants.SESSION_ID);
        firebaseHelper = new FirebaseInstructorHelper(this, sessionId);
    }

    protected void addListeners(){
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
    }

    protected void openFileChooser(){
        Intent intent = new Intent(this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        intent.setType("file/*");

        intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(intent, FILE_SELECT_CODE);
    }

    protected void updateListAdapter(Uri uri){
        String filePath = uri.getPath();
        String fileName = new File(filePath).getName();
        Log.i(TAG, "onActivityResult: "+filePath+" Choosed");

        SharedFile file = new SharedFile();
        file.setPath(filePath);
        file.setName(fileName);
        sharedFiles.add(file);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    firebaseHelper.uploadFile(uri);
                    updateListAdapter(uri);
                }
                break;
            default:
                Toast.makeText(this, "Cannot Start Session: Unsupported File Type", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    protected  void testSharedFilesAdapter(){
        ArrayList<SharedFile> fileList = new ArrayList<>();
        for(int i=0; i<20; i++){
            SharedFile file = new SharedFile();
            file.setName("File Name 1");
            fileList.add(file);
        }
        ShareFilesAdapter adapter = new ShareFilesAdapter(this, fileList);
        lv.setAdapter(adapter);
    }

    protected void testJoinedUserAdapter(){
        ArrayList<User> userList = new ArrayList<>();
        for(int i=0; i<20; i++){
            User user = new User();
            user.setName("User Name "+i);
            user.setUserId("UserId"+i);
            userList.add(user);
        }
        JoinedUsersAdapter adapter = new JoinedUsersAdapter(this, userList);
        lv.setAdapter(adapter);
    }
}
