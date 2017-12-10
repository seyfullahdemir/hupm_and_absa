package tr.edu.metu.ceng.absa.aspectextraction.dictionary;

public interface ISentimentDictionary {
    boolean containsWord(String word, String pos);

    Double getScore(String word, String pos);
}
