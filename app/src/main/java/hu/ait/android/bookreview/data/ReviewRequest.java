package hu.ait.android.bookreview.data;

import java.util.Map;

public class ReviewRequest {

    private String uid;
    private String reviewRequestAuthor;
    private String bookTitle;
    private String bookAuthor;
    private String reviewRequestBody;
    private String imgUrl;
    private String numberOfReplies;
    private Map<String, Boolean> repliesList;

    public ReviewRequest() {

    }

    public ReviewRequest(String uid, String author, String bookTitle, String bookAuthor,
                         String reviewRequestBody, String numberOfReplies, Map<String, Boolean> repliesList) {
        this.uid = uid;
        this.reviewRequestAuthor = author;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.reviewRequestBody = reviewRequestBody;
        this.numberOfReplies = numberOfReplies;
        this.repliesList = repliesList;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReviewRequestAuthor() {
        return reviewRequestAuthor;
    }

    public void setReviewRequestAuthor(String reviewRequestAuthor) {
        this.reviewRequestAuthor = reviewRequestAuthor;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getReviewRequestBody() {
        return reviewRequestBody;
    }

    public void setReviewRequestBody(String reviewRequestBody) {
        this.reviewRequestBody = reviewRequestBody;
    }

    public String getNumberOfReplies() {
        return numberOfReplies;
    }

    public void setNumberOfReplies(String numberOfReplies) {
        this.numberOfReplies = numberOfReplies;
    }


    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public Map<String, Boolean> getRepliesList() {
        return repliesList;
    }

    public void setRepliesList(Map<String, Boolean> repliesList) {
        this.repliesList = repliesList;
    }
}
