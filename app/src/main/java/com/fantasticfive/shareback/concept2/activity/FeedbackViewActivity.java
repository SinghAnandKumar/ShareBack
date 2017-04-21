package com.fantasticfive.shareback.concept2.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fantasticfive.shareback.R;
import com.fantasticfive.shareback.concept2.Constants;

import com.fantasticfive.shareback.concept2.bean.CreatedSession;
import com.fantasticfive.shareback.concept2.bean.Rating;
import com.fantasticfive.shareback.concept2.bean.SharedFile;
import com.fantasticfive.shareback.concept2.bean.UploadingFile;
import com.fantasticfive.shareback.concept2.helper.FirebaseFeedbackHelper;
import com.fantasticfive.shareback.concept2.helper.FirebaseInstructorHelper;
import com.fantasticfive.shareback.concept2.helper.FirebaseUploadHelper;
import com.fantasticfive.shareback.concept2.util.MathUtils;
import com.fantasticfive.shareback.concept2.view.adapters.CommentsAdapter;
import com.fantasticfive.shareback.concept2.view.adapters.ShareFilesInstructorAdapter;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class FeedbackViewActivity extends AppCompatActivity
        implements ShareFilesInstructorAdapter.Callback,
        FirebaseFeedbackHelper.Callback,
        FirebaseInstructorHelper.Callback {

    private static final int STORAGE_REQUEST = 1;
    final String TAG = "MY TAG";
    final int FILE_SELECT_CODE = 123;

    TextView star1, star2, star3, star4, star5, textComment, tvRating, tvUsers, docCount;
    View star1Bar, star2Bar, star3Bar, star4Bar, star5Bar;
    ListView lvComments;
    CommentsAdapter commentsAdapter;
    ImageView imgBkg;
    ListViewCompat lv;
    FloatingActionButton fabShare;
    AppCompatRatingBar rbAvgRating;
    View docsPlaceHolder;

    String sessionId, sessionName;
    ArrayList<String> comments;
    ArrayList<SharedFile> sharedFiles;
    HashSet<SharedFile> uploadingFileSet = new HashSet<>();

    ShareFilesInstructorAdapter shareFilesAdapter;
    CreatedSession createdSession;
    FirebaseInstructorHelper instructorHelper;
    FirebaseFeedbackHelper feedbackHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c2_activity_feedback);
        init();
        askPermissions();
        addListeners();

        lvComments.setAdapter(commentsAdapter);
        feedbackHelper.listenForFeedbacks(createdSession);
        getSupportActionBar().setTitle(sessionName);

        sharedFiles = new ArrayList<>();
        shareFilesAdapter = new ShareFilesInstructorAdapter(this, sharedFiles, this);
        lv.setAdapter(shareFilesAdapter);

        instructorHelper.listenForDocChange();
    }

    private void askPermissions(){
        //Toast.makeText(FeedbackViewActivity.this, "Need Some Permissions to download File", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_REQUEST);
    }

    private void init(){
        star1 = (TextView) findViewById(R.id.star1);
        star2 = (TextView) findViewById(R.id.star2);
        star3 = (TextView) findViewById(R.id.star3);
        star4 = (TextView) findViewById(R.id.star4);
        star5 = (TextView) findViewById(R.id.star5);
        star1Bar = findViewById(R.id.star1Bar);
        star2Bar = findViewById(R.id.star2Bar);
        star3Bar = findViewById(R.id.star3Bar);
        star4Bar = findViewById(R.id.star4Bar);
        star5Bar = findViewById(R.id.star5Bar);
        lvComments = (ListView) findViewById(R.id.lvComments);
        imgBkg = (ImageView) findViewById(R.id.bkg_img);
        textComment = (TextView) findViewById(R.id.textComment);
        tvRating = (TextView) findViewById(R.id.tvRating);
        rbAvgRating = (AppCompatRatingBar) findViewById(R.id.rb_feedback_rating);
        tvUsers = (TextView) findViewById(R.id.tvUsers);

        //From Instructor Activity
        docCount = (TextView) findViewById(R.id.docs_count);
        lv = (ListViewCompat) findViewById(R.id.lv);
        fabShare = (FloatingActionButton) findViewById(R.id.fab);
        docsPlaceHolder = findViewById(R.id.place_holder_docs);


        sessionId = getIntent().getExtras().getString(Constants.SESSION_ID);
        sessionName = getIntent().getExtras().getString(Constants.SESSION_NAME);
        feedbackHelper = new FirebaseFeedbackHelper(this, this);
        comments = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this, comments);
        createdSession = new CreatedSession();
        createdSession.setSessionId(sessionId);
        createdSession.setSessionName(sessionName);

        instructorHelper = new FirebaseInstructorHelper(this, sessionId, this);
    }

    @Override
    public void onCommentChange(ArrayList<String> comments) {
        this.comments.clear();
        if(comments!=null) {
            this.comments.addAll(comments);
            textComment.setVisibility(View.VISIBLE);
            imgBkg.setVisibility(View.GONE);
            commentsAdapter.notifyDataSetChanged();
        }
        else{
            imgBkg.setVisibility(View.VISIBLE);
            textComment.setVisibility(View.GONE);
            Toast.makeText(FeedbackViewActivity.this, "Nothing in Comments", Toast.LENGTH_SHORT).show();
        }
    }

    protected void addListeners(){
        fabShare.setOnClickListener(new View.OnClickListener() {
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

        UploadingFile uploadingFile = new UploadingFile();
        uploadingFile.setLocalUri(uri);
        uploadingFile.setName(fileName);

        uploadingFileSet.add(uploadingFile);
        sharedFiles.add(uploadingFile);
        if(sharedFiles.size() <= 0){
            docsPlaceHolder.setVisibility(View.GONE);
        }
        docCount.setText(sharedFiles.size()+"");
        shareFilesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    updateListAdapter(uri);
                }
                break;
            default:
                Toast.makeText(this, "Cannot Start CreatedSession: Unsupported File Type", Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRatingChange(Rating ratings) {
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(1);
        double avgRating = MathUtils.avg(ratings);

        tvRating.setText(format.format(avgRating));
        rbAvgRating.setRating((float)avgRating);

        star1.setText(ratings.getRating1()+"");
        star2.setText(ratings.getRating2()+"");
        star3.setText(ratings.getRating3()+"");
        star4.setText(ratings.getRating4()+"");
        star5.setText(ratings.getRating5()+"");

        ViewGroup.LayoutParams params;
        float star1pc = MathUtils.percentage(ratings.getRating1(), ratings);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, star1pc);
        star1Bar.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 107f-star1pc);
        star1.setLayoutParams(params);

        float star2pc = MathUtils.percentage(ratings.getRating2(), ratings);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, star2pc);
        star2Bar.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 107f-star2pc);
        star2.setLayoutParams(params);

        float star3pc = MathUtils.percentage(ratings.getRating3(), ratings);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, star3pc);
        star3Bar.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 107f-star3pc);
        star3.setLayoutParams(params);

        float star4pc = MathUtils.percentage(ratings.getRating4(), ratings);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, star4pc);
        star4Bar.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 107f-star4pc);
        star4.setLayoutParams(params);

        float star5pc = MathUtils.percentage(ratings.getRating5(), ratings);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, star5pc);
        star5Bar.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 107f-star5pc);
        star5.setLayoutParams(params);

    }

    @Override
    public void onUsersJoined(ArrayList<String> users) {
        if(users == null){
            //Toast.makeText(this, "Session is Closed Permanently", Toast.LENGTH_SHORT).show();
            //finish();
            return;
        }
        tvUsers.setText(users.size()+"");
    }
    @Override
    public void onDocChange(ArrayList<SharedFile> sharedFiles) {
        if(sharedFiles!=null){
            for(SharedFile file : sharedFiles){
                if(uploadingFileSet.contains(file))
                    uploadingFileSet.remove(file);
            }
            Iterator<SharedFile> itr = uploadingFileSet.iterator();
            while(itr.hasNext()){
                SharedFile file = itr.next();
                sharedFiles.add(file);
            }

            this.sharedFiles.clear();
            HashSet<String> set = new HashSet<>();
            for(SharedFile sf: sharedFiles){
                if(!sharedFiles.contains(sf.getName())){
                    this.sharedFiles.add(sf);
                    set.add(sf.getName());
                }
            }

            shareFilesAdapter.notifyDataSetChanged();
            docCount.setText(this.sharedFiles.size()+"");
            if(this.sharedFiles.size() == 0) {
                docsPlaceHolder.setVisibility(View.VISIBLE);
            }
            else{
                docsPlaceHolder.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(FeedbackViewActivity.this, "Cool", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onUploadComplete(String downloadPath) {
        String name = new File(downloadPath).getName();
        SharedFile sharedFile = new SharedFile();
        sharedFile.setName(name);
        sharedFile.setPath(downloadPath);
        instructorHelper.insertInSharedFile(sharedFile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.c2_menu_instructor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.close_session: closeSession(); break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeSession(){
        instructorHelper.removeSessionEntry();
        finish();
    }
}
