package tr.edu.metu.ceng.absa.aspectextraction.util;

import tr.edu.metu.ceng.absa.aspectextraction.entity.AspectSentimentMatch;

import java.io.IOException;
import java.util.List;

public interface IFileWriter {
    void writeResults(String filePath, List<AspectSentimentMatch> aspectSentimentMatches) throws IOException;
}
