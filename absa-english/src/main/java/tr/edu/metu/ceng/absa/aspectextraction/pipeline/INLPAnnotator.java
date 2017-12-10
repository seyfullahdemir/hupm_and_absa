package tr.edu.metu.ceng.absa.aspectextraction.pipeline;

import tr.edu.metu.ceng.absa.aspectextraction.entity.Review;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Sentence;

import java.util.List;

public interface INLPAnnotator {
    List<Sentence> annotateSentences(Review review);
}
