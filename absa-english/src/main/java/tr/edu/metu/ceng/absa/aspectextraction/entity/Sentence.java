package tr.edu.metu.ceng.absa.aspectextraction.entity;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;

public class Sentence{

    private Review review;
    private CoreMap coreMap;
    private String sentenceText;
    private List<NounPhrase> nounPhrases;
    private List<SentimentPhrase> sentimentPhrases;
    private List<AspectSentimentMatch> aspectSentimentMatches;

    public Sentence(Review review, CoreMap coreMap) {
        this.review = review;
        this.coreMap = coreMap;
        this.sentenceText = coreMap.get(CoreAnnotations.TextAnnotation.class);
    }

    public List<AspectSentimentMatch> getAspectSentimentMatches() {
        return aspectSentimentMatches;
    }

    public String getSentenceText() {
        return sentenceText;
    }

    public CoreMap getCoreMap() {
        return coreMap;
    }

    public void setNounPhrases(List<NounPhrase> nounPhrases) {
        this.nounPhrases = nounPhrases;
    }

    public void setSentimentPhrases(List<SentimentPhrase> sentimentPhrases) {
        this.sentimentPhrases = sentimentPhrases;
    }

    public void setAspectSentimentMatches(List<AspectSentimentMatch> aspectSentimentMatches) {
        this.aspectSentimentMatches = aspectSentimentMatches;
    }

    public List<NounPhrase> getNounPhrases() {
        return nounPhrases;
    }

    public List<SentimentPhrase> getSentimentPhrases() {
        return sentimentPhrases;
    }

    public Review getReview() {
        return review;
    }

    @Override
    public String toString() {
        return "SENTENCE: " + getSentenceText();
    }
}
