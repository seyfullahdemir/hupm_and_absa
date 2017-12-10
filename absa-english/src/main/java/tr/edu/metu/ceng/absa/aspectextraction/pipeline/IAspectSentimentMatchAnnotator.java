package tr.edu.metu.ceng.absa.aspectextraction.pipeline;

import tr.edu.metu.ceng.absa.aspectextraction.entity.Sentence;

public interface IAspectSentimentMatchAnnotator {
    void annotateMatches(Sentence sentence);
}
