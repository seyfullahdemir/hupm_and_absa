package tr.edu.metu.ceng.absa.aspectextraction.entity;

public class AspectSentimentMatch {
    private Sentence sentence;
    private Aspect aspect;
    private SentimentPhrase sentimentPhrase;

    public AspectSentimentMatch(final Sentence sentence, Aspect aspect, SentimentPhrase sentimentPhrase) {
        this.sentence = sentence;
        this.aspect = aspect;
        this.sentimentPhrase = sentimentPhrase;
    }

    @Override
    public String toString() {
        return sentence.getReview() + " " + sentence + " " + aspect + " " + sentimentPhrase;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public Aspect getAspect() {
        return aspect;
    }

    public SentimentPhrase getSentimentPhrase() {
        return sentimentPhrase;
    }
}
