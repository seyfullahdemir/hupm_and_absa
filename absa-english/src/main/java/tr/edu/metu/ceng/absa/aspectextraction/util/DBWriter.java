package tr.edu.metu.ceng.absa.aspectextraction.util;

import tr.edu.metu.ceng.absa.aspectextraction.entity.AspectSentimentMatch;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Sentence;
import tr.edu.metu.ceng.absa.aspectextraction.entity.SentimentPhrase;
import tr.edu.metu.ceng.absa.common.datatypes.ABSAConstants;
import tr.edu.metu.ceng.absa.common.dbutil.SqlConnectionHelper;

import java.io.IOException;
import java.util.List;

public class DBWriter implements IFileWriter {


    private final static String AMAZON_REVIEW_RESULTS = "AMAZON_REVIEW_RESULTS";
    private SqlConnectionHelper sqlConn = null;

    public DBWriter(){
        sqlConn = new SqlConnectionHelper("localhost", ABSAConstants.DB_NAME,
                "root", "123456");
    }
    @Override
    public void writeResults(String filePath, List<AspectSentimentMatch> aspectSentimentMatches) throws IOException {

        createTable();

        for (AspectSentimentMatch aspectSentimentMatch : aspectSentimentMatches) {
            insertToDB(aspectSentimentMatch);
        }
        //create db first, then insert the results
    }

    private void insertToDB(AspectSentimentMatch aspectSentimentMatch) {
        sqlConn.executeUpdateQuery(generateInsertQuery(aspectSentimentMatch));
    }

    private String generateInsertQuery(AspectSentimentMatch aspectSentimentMatch) {
        final Sentence sentence = aspectSentimentMatch.getSentence();
        String sentenceText = sentence.getSentenceText();
        final int reviewId = sentence.getReview().getReviewId();
        String aspectWord = aspectSentimentMatch.getAspect().getWord();

        aspectWord = aspectWord.replaceAll("\'", "");

        sentenceText = sentenceText.replaceAll("\'", "");


        final SentimentPhrase sentimentPhrase = aspectSentimentMatch.getSentimentPhrase();
        final String sentimentWord = sentimentPhrase.getWord();
        final double sentimentScore = sentimentPhrase.getScore();
        return "INSERT INTO " + AMAZON_REVIEW_RESULTS
                + " VALUES (" + reviewId + ",'" + StringUtil.getInstance().shorten(sentenceText, 1999) + "','" + aspectWord + "'," +   (sentimentPhrase.isNegated() == false?0:1) + ",'" + sentimentWord + "'," + sentimentScore + ")";
    }


    //TODO a primary key may be added, but it can be added in sql later as well. Thus, I didn't change the code.
    private void createTable() {
        sqlConn.executeUpdateQuery("DROP TABLE "
                + DBWriter.AMAZON_REVIEW_RESULTS);
        sqlConn.executeUpdateQuery("CREATE TABLE "
                + DBWriter.AMAZON_REVIEW_RESULTS
                + "( REVIEW_ID INT(11) NOT NULL, "
                + "sentence VARCHAR(2000) NOT NULL, "
                + "aspect VARCHAR(250) NOT NULL, "
                + "negated INT(5) NOT NULL, "
                + "sentiment_word VARCHAR(100) NOT NULL, "
                + "sentiment_score DOUBLE NOT NULL ) "
                + "ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci");
    }
}
