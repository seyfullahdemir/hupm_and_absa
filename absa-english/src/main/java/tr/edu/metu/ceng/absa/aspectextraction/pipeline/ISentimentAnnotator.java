package tr.edu.metu.ceng.absa.aspectextraction.pipeline;

import tr.edu.metu.ceng.absa.aspectextraction.entity.Sentence;

public interface ISentimentAnnotator {
    void annotateSentiments(Sentence sentence);
}
