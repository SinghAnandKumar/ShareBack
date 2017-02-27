package com.fantasticfive.shareback.concept2.helper;

import android.content.Context;
import android.util.Log;

import com.fantasticfive.shareback.concept2.bean.CreatedSession;
import com.fantasticfive.shareback.concept2.bean.Rating;
import com.fantasticfive.shareback.concept2.util.FirebaseKeys;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by sagar on 24/2/17.
 */
public class FirebaseFeedbackHelper {

    private static final String TAG = "MY TAG";
    DatabaseReference rootRef;
    FirebaseDatabase db;
    Callback callback;
    Context context;
    public FirebaseFeedbackHelper(Context context, Callback callback){
        db = FirebaseDatabase.getInstance();
        rootRef = db.getReference();
        this.context = context;
        this.callback = callback;
    }

    public void listenForFeedbacks(CreatedSession session){
        DatabaseReference commentRef = rootRef.child(FirebaseKeys.userSessionComments(session));
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> type = new GenericTypeIndicator<ArrayList<String>>() {};
                ArrayList<String> comments = dataSnapshot.getValue(type);
                callback.onCommentChange(comments);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: "+databaseError.getMessage() );
            }
        });

        DatabaseReference ratingRef = rootRef.child(FirebaseKeys.userSessionRating(session));
        ratingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Rating rating = dataSnapshot.getValue(Rating.class);
                callback.onRatingChange(rating);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: "+databaseError.getMessage() );
            }
        });
        DatabaseReference joinedUserRef = rootRef.child(FirebaseKeys.joinedUsers(session));
        joinedUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> type = new GenericTypeIndicator<ArrayList<String>>() {};
                ArrayList<String> users = dataSnapshot.getValue(type);
                if(users !=null)
                    callback.onUsersJoined(users);
                else
                    callback.onUsersJoined(new ArrayList<String>());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: "+databaseError.getMessage() );
            }
        });
    }

    public interface Callback{
        void onCommentChange(ArrayList<String> comments);
        void onRatingChange(Rating rating);
        void onUsersJoined(ArrayList<String> users);
    }
}
