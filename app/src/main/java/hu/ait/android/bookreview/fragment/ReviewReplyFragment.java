package hu.ait.android.bookreview.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import hu.ait.android.bookreview.R;
import hu.ait.android.bookreview.ReviewRequestsActivity;

public class ReviewReplyFragment extends DialogFragment {

    private OnReviewReplyFragmentAnswer onReviewReplyFragmentAnswer = null;
    public static final String TAG = "ReviewReplyFragment";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnReviewReplyFragmentAnswer) {
            onReviewReplyFragmentAnswer = (OnReviewReplyFragmentAnswer) context;
        } else {
            throw new RuntimeException(
                    "This Activity is not implementing the " +
                            "OnReviewReplyFragmentAnswer interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(ReviewRequestsActivity.KEY_MSG);
        final int request_being_replied_to = getArguments().getInt(ReviewRequestsActivity.REQUEST_BEING_REPLIED);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogLayout = inflater.inflate(R.layout.layout_reply_text, null);
        final EditText etReviewReplyBody = dialogLayout.findViewById(R.id.etReviewReplyBody);

        alertDialogBuilder.setView(dialogLayout);

        alertDialogBuilder.setTitle(R.string.book_review);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                onReviewReplyFragmentAnswer.onPositiveSelected(etReviewReplyBody.getText().toString(), request_being_replied_to);
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                onReviewReplyFragmentAnswer.onNegativeSelected();
            }
        });


        return alertDialogBuilder.create();
    }
}
