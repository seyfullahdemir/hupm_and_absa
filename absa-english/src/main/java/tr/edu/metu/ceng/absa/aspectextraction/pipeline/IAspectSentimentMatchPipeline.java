package tr.edu.metu.ceng.absa.aspectextraction.pipeline;

import tr.edu.metu.ceng.absa.aspectextraction.entity.AspectSentimentMatch;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Review;

import java.util.List;

public interface IAspectSentimentMatchPipeline {

    List<AspectSentimentMatch> extractMatches(Review review);
}
