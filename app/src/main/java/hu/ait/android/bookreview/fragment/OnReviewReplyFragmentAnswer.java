package hu.ait.android.bookreview.fragment;

public interface OnReviewReplyFragmentAnswer {
    void onPositiveSelected(String reply, int request_being_replied_to);
    void onNegativeSelected();
}
