package hu.ait.android.bookreview;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.Objects;

import hu.ait.android.bookreview.adapter.ReviewRequestsAdapter;
import hu.ait.android.bookreview.data.ReviewRequest;
import hu.ait.android.bookreview.fragment.OnReviewReplyFragmentAnswer;
import hu.ait.android.bookreview.fragment.ReviewReplyFragment;

public class ReviewRequestsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnReviewReplyFragmentAnswer {

    public static final String REQUEST_BEING_REPLIED = "REQUEST_BEING_REPLIED";
    public static final String REQUEST_TO_VIEW = "REQUEST_TO_VIEW";
    private ReviewRequestsAdapter adapter;
    private NotificationHelper notificationHelper;

    private static final int NOTIFICATION_NEW_POST = 1100;
    private static final int NOTIFICATION_REPLY_TO_POST = 1101;
    public static final String KEY_MSG = "KEY_MSG";
    private CoordinatorLayout layoutContent;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_requests);

        notificationHelper = new NotificationHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReviewRequestsActivity.this, CreateReviewRequestActivity.class));
            }
        });

        layoutContent = findViewById(R.id.repliesNestedScrollView);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("review_requests_");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView tvUserEmail = navigationView.getHeaderView(0).findViewById(R.id.tvUserEmail);
        tvUserEmail.setText(FirebaseAuth.getInstance().
                getCurrentUser().
                getEmail());

        RecyclerView recyclerView = findViewById(R.id.recyclerViewPosts);
        adapter = new ReviewRequestsAdapter(this, FirebaseAuth.getInstance().getCurrentUser().getUid());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        initPostListener();

    }

    private void sendNotification(int id) {
        Notification.Builder notificationBuilder = null;
        switch (id) {
            case NOTIFICATION_NEW_POST:
                notificationBuilder =
                        notificationHelper.getNotificationNewPost(
                                getString(R.string.new_post_title_notification),
                                getString(R.string.new_post_added_notification_body));
                break;

            case NOTIFICATION_REPLY_TO_POST:
                notificationBuilder =
                        notificationHelper.getNotificationReply(
                                getString(R.string.reply_title_notification),
                                getString(R.string.reply_added_notification_body));
                break;


        }
        if (notificationBuilder != null) {
            notificationHelper.notify(id, notificationBuilder);
        }
    }


    private void initPostListener() {

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ReviewRequest reviewRequest = dataSnapshot.getValue(ReviewRequest.class);
                adapter.addReviewRequest(reviewRequest, dataSnapshot.getKey());

                if (!Objects.equals(reviewRequest.getUid(), adapter.getUid())) {
                    sendNotification(NOTIFICATION_NEW_POST);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ReviewRequest reviewRequest = dataSnapshot.getValue(ReviewRequest.class);
                adapter.modifyReviewRequestByKey(reviewRequest, dataSnapshot.getKey());

                if (Objects.equals(reviewRequest.getUid(), adapter.getUid())) {
                    sendNotification(NOTIFICATION_REPLY_TO_POST);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.removeReviewRequestByKey(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openReplyDialog(int adapterPosition) {

        ReviewReplyFragment reviewReplyFragment = new ReviewReplyFragment();
        reviewReplyFragment.setCancelable(false);

        Bundle bundle = new Bundle();
        bundle.putString(KEY_MSG, getString(R.string.review_thanks));
        bundle.putInt(REQUEST_BEING_REPLIED, adapterPosition);

        reviewReplyFragment.setArguments(bundle);

        reviewReplyFragment.show(getSupportFragmentManager(), ReviewReplyFragment.TAG);
    }

    @Override
    public void onPositiveSelected(String review, int request_being_replied_to) {

        databaseReference.child(adapter.getReviewRequestKeys().get(request_being_replied_to)).
                child("repliesList").child(review).setValue(true);

        databaseReference.child(adapter.getReviewRequestKeys().get(request_being_replied_to)).
                child("numberOfReplies").runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                String currentNumberOfReplies = mutableData.getValue(String.class);
                int count;

                if (currentNumberOfReplies == null) {
                    return Transaction.success(mutableData);

                } else {
                    count = Integer.valueOf(currentNumberOfReplies) + 1;
                }

                mutableData.setValue(String.valueOf(count));
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(getString(R.string.chill), "postTransaction:onComplete:" + databaseError);

            }
        });

    }

    @Override
    public void onNegativeSelected() {
        showSnackBarMessage(getString(R.string.okay_cool));
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(layoutContent,
                message,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.hide, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
            }
        }).show();
    }

    public void openReviewRepliesActivity(int adapterPosition) {
        Intent intentReviewReplies = new Intent(ReviewRequestsActivity.this,
                ReviewRepliesActivity.class);

        String requestToView = databaseReference.child(adapter.getReviewRequestKeys().get(adapterPosition)).getKey();

        intentReviewReplies.putExtra(REQUEST_TO_VIEW, requestToView);

        startActivity(intentReviewReplies);

    }
}