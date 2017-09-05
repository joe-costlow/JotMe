package com.josephcostlow.jotme;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph Costlow on 27-Aug-17.
 */

public class WidgetListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, ValueEventListener {

    private Context mContext;
    private List<Jot> jotList;
    private int mAppWidgetId;
    private FirebaseDatabase mJotsDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;
    private DatabaseReference mJotsDatabaseReference;
    private DatabaseReference mJotsDatabaseUsersReference;
    private DatabaseReference mJotsDatabaseSpecificUserReference;
    private String mUserID;
    private Query mQuery;

    public WidgetListRemoteViewsFactory(Context context, Intent intent) {

        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    private void populateWidgetList() {

        jotList = new ArrayList<>();

        mJotsDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUser = mFirebaseAuth.getCurrentUser();

        if (mUser != null) {

            mUserID = mUser.getUid();

            mJotsDatabaseReference = mJotsDatabase.getReference();
            mJotsDatabaseUsersReference = mJotsDatabaseReference.child("users");
            mJotsDatabaseSpecificUserReference = mJotsDatabaseUsersReference.child(mUserID);

            mQuery = mJotsDatabaseSpecificUserReference.limitToLast(3);

            mQuery.addValueEventListener(this);

            synchronized (this) {

                try {
                    this.wait();

                } catch (InterruptedException e) {

                    Log.v(e.toString(), e.toString());
                }
            }
        }
    }

    private void populateWidgetLocalList(DataSnapshot dataSnapshot) {

        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            Jot jot = new Jot();

            jot = ds.getValue(Jot.class);

            jotList.add(jot);
        }

        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        populateWidgetList();
    }

    @Override
    public void onDestroy() {

        if (jotList != null) {
            jotList.clear();
        }
    }

    @Override
    public int getCount() {

        return jotList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

        if (jotList.size() > 0) {

            remoteViews.setTextViewText(R.id.widget_list_item_title, jotList.get(position).getTitle());

            final Intent fillInIntent = new Intent();

            fillInIntent.putExtra("position", position);

            remoteViews.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
            remoteViews.setOnClickFillInIntent(R.id.widget_list_item_title, fillInIntent);
        }

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        populateWidgetLocalList(dataSnapshot);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
