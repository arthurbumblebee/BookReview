package hu.ait.android.bookreview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import hu.ait.android.bookreview.R;
import hu.ait.android.bookreview.ReviewRequestsActivity;
import hu.ait.android.bookreview.data.ReviewRequest;

public class ReviewRequestsAdapter
        extends RecyclerView.Adapter<ReviewRequestsAdapter.ReviewRequestsHolder> {

    private Context context;
    private List<ReviewRequest> reviewRequestList;
    private List<String> reviewRequestKeys;
    private String uId;
    private int lastPosition = -1;
    private DatabaseReference reviewRequestReference;

    private String bookTitle;
    private String bookAuthor;
    private String numberOfReplies;

    public ReviewRequestsAdapter(Context context, String uId) {
        this.context = context;
        this.uId = uId;

        reviewRequestList = new ArrayList<>();
        reviewRequestKeys = new ArrayList<>();
        reviewRequestReference = FirebaseDatabase.getInstance().getReference().child("review_requests_");

    }

    public String getUid() {
        return uId;
    }

    @Override
    public ReviewRequestsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        bookTitle = parent.getResources().getString(R.string.book_title);
        bookAuthor = parent.getResources().getString(R.string.author);
        numberOfReplies = parent.getResources().getString(R.string.replies);


        View row = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.row_review_request, parent, false);

        return new ReviewRequestsHolder(row);
    }

    @Override
    public void onBindViewHolder(final ReviewRequestsHolder holder, int position) {

        ReviewRequest reviewRequest = reviewRequestList.get(position);

        holder.tvReviewRequestAuthor.setText(reviewRequest.getReviewRequestAuthor());
        holder.tvReplies.setText(String.format("%s %s", numberOfReplies, reviewRequest.getNumberOfReplies()));
        holder.tvBookTitle.setText(String.format("%s %s", bookTitle, reviewRequest.getBookTitle()));
        holder.tvBookAuthor.setText(String.format("  %s %s", bookAuthor, reviewRequest.getBookAuthor()));
        holder.tvReviewRequestBody.setText(reviewRequest.getReviewRequestBody());

        if (reviewRequest.getImgUrl() != null) {
            Glide.with(context).load(reviewRequest.getImgUrl()).into(holder.ivRequestBookImg);
            holder.ivRequestBookImg.setVisibility(View.VISIBLE);
        } else {
            holder.ivRequestBookImg.setVisibility(View.GONE);
        }

        if (uId.equals(reviewRequest.getUid())) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnReply.setVisibility(View.GONE);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnReply.setVisibility(View.VISIBLE);
        }

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeReviewRequest(holder.getAdapterPosition());
            }
        });

        holder.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ReviewRequestsActivity) context).openReplyDialog(holder.getAdapterPosition());
            }
        });

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ReviewRequestsActivity) context).openReviewRepliesActivity(holder.getAdapterPosition());
            }
        });

        setAnimation(holder.itemView, position);

    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void removeReviewRequest(int index) {
        reviewRequestReference.child(reviewRequestKeys.get(index)).removeValue();
        reviewRequestList.remove(index);
        reviewRequestKeys.remove(index);
        notifyItemRemoved(index);
    }

    public void removeReviewRequestByKey(String key) {
        int index = reviewRequestKeys.indexOf(key);
        if (index != -1) {
            reviewRequestList.remove(index);
            reviewRequestKeys.remove(index);
            notifyItemRemoved(index);
        }
    }


    public void modifyReviewRequestByKey(ReviewRequest reviewRequest, String key) {
        int index = reviewRequestKeys.indexOf(key);
        if (index != -1) {
            ReviewRequest reviewRequestToModify = reviewRequestList.get(index);

            reviewRequestToModify.setBookAuthor(reviewRequest.getBookAuthor());
            reviewRequestToModify.setUid(reviewRequest.getUid());
            reviewRequestToModify.setReviewRequestAuthor(reviewRequest.getReviewRequestAuthor());
            reviewRequestToModify.setBookTitle(reviewRequest.getBookTitle());
            reviewRequestToModify.setNumberOfReplies(reviewRequest.getNumberOfReplies());
            reviewRequestToModify.setRepliesList(reviewRequest.getRepliesList());
            reviewRequestToModify.setReviewRequestBody(reviewRequest.getReviewRequestBody());

            notifyDataSetChanged();

        }
    }

    public void addReviewRequest(ReviewRequest reviewRequest, String key) {
        reviewRequestList.add(reviewRequest);
        reviewRequestKeys.add(key);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reviewRequestList.size();
    }


    static class ReviewRequestsHolder extends RecyclerView.ViewHolder {
        TextView tvReviewRequestAuthor;
        TextView tvBookTitle;
        TextView tvBookAuthor;
        TextView tvReviewRequestBody;
        TextView tvReplies;
        Button btnDelete;
        Button btnReply;
        Button btnView;
        ImageView ivRequestBookImg;

        ReviewRequestsHolder(View itemView) {
            super(itemView);

            tvReviewRequestAuthor = itemView.findViewById(R.id.tvReviewRequestAuthor);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvReviewRequestBody = itemView.findViewById(R.id.tvReviewRequestBody);
            tvReplies = itemView.findViewById(R.id.tvReplies);
            tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnReply = itemView.findViewById(R.id.btnReply);
            btnView = itemView.findViewById(R.id.btnView);
            ivRequestBookImg = itemView.findViewById(R.id.ivRequestBookImg);

        }
    }

    public List<ReviewRequest> getReviewRequestList() {
        return reviewRequestList;
    }

    public List<String> getReviewRequestKeys() {
        return reviewRequestKeys;
    }

}
