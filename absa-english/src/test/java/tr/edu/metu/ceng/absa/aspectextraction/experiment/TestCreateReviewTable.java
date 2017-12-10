package tr.edu.metu.ceng.absa.aspectextraction.experiment;

import tr.edu.metu.ceng.absa.aspectextraction.entity.Review;
import tr.edu.metu.ceng.absa.common.dbutil.SqlConnectionHelper;

//only run consciously
public class TestCreateReviewTable {

    private SqlConnectionHelper sqlConn = null;

    //only run consciously

    /*
    @Test
    public void testCreateReviewTable() throws IOException {

        final String inputFileName = "src\\main\\resources\\Cell_Phones_and_Accessories_5.json";
        final String outputFileName = "src\\main\\resources\\noway.csv";
        ExperimentRunner experimentRunner =
                new ExperimentRunner(inputFileName, outputFileName);

        final List<Review> reviews = experimentRunner.readReviews();



        sqlConn = new SqlConnectionHelper("localhost", ABSAConstants.DB_NAME, "root", "123456");

        createTable();


        for (Review review : reviews) {
            insertToDB(review);
        }

    }
    */

    private void insertToDB(Review review) {
        sqlConn.executeUpdateQuery(generateInsertQuery(review));
    }

    private String generateInsertQuery(Review review) {

        String reviewText = review.getReviewText();
        String summary = review.getSummary();
        reviewText = reviewText.replaceAll("\'", "");
        summary = summary.replaceAll("\'", "");

        final int reviewId = review.getReviewId();
        int helpfulFrom = review.getHelpfulFrom();
        int helpfulTo = review.getHelpfulTo();
        final double overall = review.getOverall();
        return "INSERT INTO " + "AMAZON_REVIEW"
                + " VALUES (" + reviewId + ",'" + reviewText + "'," + helpfulFrom + "," +   helpfulTo + "," + overall + ",'" + summary + "')";

    }

    private void createTable() {
        sqlConn.executeUpdateQuery("DROP TABLE "
                + "AMAZON_REVIEW");
        sqlConn.executeUpdateQuery("CREATE TABLE "
                + "AMAZON_REVIEW"
                + "( REVIEW_ID INT(11) NOT NULL, "
                + "review_text TEXT(32120) NOT NULL, "
                + "helpful_from INT(2) NOT NULL, "
                + "helpful_to INT(2) NOT NULL, "
                + "overall DOUBLE NOT NULL, "
                + "summary VARCHAR(130) NOT NULL ) "
                + "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci");
    }
}
