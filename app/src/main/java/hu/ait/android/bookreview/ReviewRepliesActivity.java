package hu.ait.android.bookreview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class ReviewRepliesActivity extends AppCompatActivity {

    public static final String WARNING = "Failed";
    private LinearLayout repliesNestedScrollView;
    private DatabaseReference reviewRequestsDatabaseReference;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_replies);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        repliesNestedScrollView = findViewById(R.id.repliesNestedScrollView);
        reviewRequestsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("review_requests_");
        firebaseAuth = FirebaseAuth.getInstance();

        final View reviewReply = getLayoutInflater().inflate(
                R.layout.review_reply, null, false);
        final TextView tvReviewReplyBody = reviewReply.findViewById(R.id.tvReviewReplyBody);

        if (getIntent().hasExtra(ReviewRequestsActivity.REQUEST_TO_VIEW)) {
            final String requestToView = getIntent().getStringExtra(ReviewRequestsActivity.REQUEST_TO_VIEW);

            reviewRequestsDatabaseReference.child(requestToView).child("repliesList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String reply = snapshot.getKey();

                            final View reviewReply = getLayoutInflater().inflate(
                                    R.layout.review_reply, null, false);

                            TextView tvReviewReplyBody = reviewReply.findViewById(R.id.tvReviewReplyBody);
                            tvReviewReplyBody.setText(reply);

                            final Button btnDelete = reviewReply.findViewById(R.id.btnDelete);
                            snapshot.getRef().getParent().getParent().child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String uid = dataSnapshot.getValue(String.class);
                                    if (firebaseAuth.getCurrentUser().getUid().equals(uid)) {
                                        btnDelete.setVisibility(View.VISIBLE);
                                    } else {
                                        btnDelete.setVisibility(View.GONE);
                                    }

                                    repliesNestedScrollView.addView(reviewReply);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(WARNING, "loadPost:onCancelled", databaseError.toException());
                                }
                            });

                            btnDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    snapshot.getRef().removeValue();

                                    snapshot.getRef().getParent().getParent().child("numberOfReplies").runTransaction(new Transaction.Handler() {
                                        @Override
                                        public Transaction.Result doTransaction(MutableData mutableData) {
                                            String currentNumberOfReplies = mutableData.getValue(String.class);
                                            int count;

                                            if (currentNumberOfReplies == null) {
                                                return Transaction.success(mutableData);

                                            } else {
                                                count = Integer.valueOf(currentNumberOfReplies) - 1;
                                            }

                                            mutableData.setValue(String.valueOf(count));
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                            Log.d(getString(R.string.chill), "postTransaction:onComplete:" + databaseError);

                                        }
                                    });
                                    repliesNestedScrollView.removeView(reviewReply);
                                }
                            });

                        }
                    } else {
                        tvReviewReplyBody.setText(R.string.sajnos_no_replies);
                        repliesNestedScrollView.addView(reviewReply);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(WARNING, "loadPost:onCancelled", databaseError.toException());
                }
            });

        } else {
            tvReviewReplyBody.setText(R.string.sajnos_no_replies);
            repliesNestedScrollView.addView(reviewReply);
        }

    }
}