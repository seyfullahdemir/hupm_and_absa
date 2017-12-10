package tr.edu.metu.ceng.absa.aspectextraction.entity;

public class SentimentPhrase implements ISequenceElement {

    private final String word;
    private final String pos;
    private final double score;
    private final int index;
    private final boolean negated;
    private static final char SEQUENCE_ELEMENT_TYPE = 'S';

    /**
     * TODO Also Sentence reference can be added as an atrribute;
     *   so that the "Overall Sentence Sentiment Score" can be calculated via Stanford CoreNLP library,
     *   and any other NLP capabilities can be gained as well.
     */

    public SentimentPhrase(final String word, final String pos, final double score, final int index) {

        this.word = word;
        this.pos = pos;
        this.score = score;
        this.index = index;
        this.negated = false;
    }

    public SentimentPhrase(final String word, final String pos, final double score, final int index, final boolean negated) {
        this.word = word;
        this.pos = pos;
        this.score = score;
        this.index = index;
        this.negated = negated;
    }

    public String getWord() {
        return word;
    }

    public String getPos() {
        return pos;
    }

    public double getScore() {
        return score;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public char getElementType() {
        return SEQUENCE_ELEMENT_TYPE;
    }

    @Override
    public int getExternalIndex() {
        return getIndex();
    }

    @Override
    public String toString() {
        return "SENTIMENT: " + word + " SCORE: " + score;
    }

    public boolean isNegated() {
        return negated;
    }
}
