package tr.edu.metu.ceng.absa.aspectextraction.experiment;

import tr.edu.metu.ceng.absa.aspectextraction.entity.AspectSentimentMatch;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Review;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.AspectSentimentMatchPipeline;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.IAspectSentimentMatchPipeline;
import tr.edu.metu.ceng.absa.aspectextraction.util.IFileWriter;
import tr.edu.metu.ceng.absa.aspectextraction.util.JSonUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExperimentRunner implements IExperimentRunner {
    private String inputFileName;
    private String outputFileName;

    public ExperimentRunner(final String inputFileName, final String outputFileName) {
        this.inputFileName = inputFileName;
        this.outputFileName = outputFileName;
    }

    @Override
    public void run(IFileWriter fileWriter) throws IOException {

        final List<AspectSentimentMatch> aspectSentimentMatches = new ArrayList();
        final List<Review> reviews = readReviews();
        for (Review review : reviews) {
            IAspectSentimentMatchPipeline pipeline = new AspectSentimentMatchPipeline();
            final List<AspectSentimentMatch> matches = pipeline.extractMatches(review);
            aspectSentimentMatches.addAll(matches);

        }

        System.out.println("Total review count: " + reviews.size());
        System.out.println("Total match count: " + aspectSentimentMatches.size());

        fileWriter.writeResults(outputFileName, aspectSentimentMatches);


    }

    protected List<Review> readReviews() throws IOException {
        FileReader in = new FileReader(inputFileName);
        BufferedReader br = new BufferedReader(in);

        List<Review> amazonReviews = new ArrayList();
        String line;
        int reviewId=1;
        while ((line = br.readLine()) != null) {
            final AmazonReview amazonReview = JSonUtil.getInstance().convertJsonStringToObject(AmazonReview.class, line);
            amazonReviews.add(new Review(reviewId, amazonReview.getReviewText(), amazonReview.getSummary(),
                    amazonReview.getOverall(), amazonReview.getHelpful()[0], amazonReview.getHelpful()[1]));

            reviewId++;
        }
        in.close();
        return amazonReviews;
    }
}

