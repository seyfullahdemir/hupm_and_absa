package tr.edu.metu.ceng.absa.aspectextraction.pipeline.matchfounder;

import java.util.List;

public interface INPAndSentimentMatchFounder {

    List<Integer> findMatches(String sequenceAsString);
}
