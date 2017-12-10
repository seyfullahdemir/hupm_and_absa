package tr.edu.metu.ceng.absa.aspectextraction.entity;

public class Review {

    private int reviewId;
    private String reviewText;

    private String summary;
    private double overall;
    private int helpfulFrom;
    private int helpfulTo;

    public Review(String reviewText) {
        this.reviewText = reviewText;
        reviewId = 1;
    }

    public Review(final int reviewId, final String reviewText, String summary, double overall, int helpfulFrom, int helpfulTo) {

        this.reviewId = reviewId;
        this.reviewText = reviewText;
        this.summary = summary;
        this.overall = overall;
        this.helpfulFrom = helpfulFrom;
        this.helpfulTo = helpfulTo;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getReviewId() {
        return reviewId;
    }

    public String getSummary() {
        return summary;
    }

    public double getOverall() {
        return overall;
    }

    public int getHelpfulFrom() {
        return helpfulFrom;
    }

    public int getHelpfulTo() {
        return helpfulTo;
    }

    @Override
    public String toString() {
        return "REVIEW ID: " + reviewId;
    }
}

