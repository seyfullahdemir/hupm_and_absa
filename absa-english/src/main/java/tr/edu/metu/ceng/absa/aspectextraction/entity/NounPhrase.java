package tr.edu.metu.ceng.absa.aspectextraction.entity;

public class NounPhrase implements ISequenceElement {
    private static final char SEQUENCE_ELEMENT_TYPE = 'N';

    /**
     * TODO Also Sentence reference can be added as an atrribute;
     *   so that some NLP capabilities can be gained.
     */

    private String word;
    private String lemma;
    private int index;

    public NounPhrase(final String word, String lemma, final int index) {

        this.word = word;
        this.lemma = lemma;
        this.index = index;
    }

    @Override
    public char getElementType() {
        return SEQUENCE_ELEMENT_TYPE;
    }

    @Override
    public int getExternalIndex() {
        return getIndex();
    }

    public int getIndex() {
        return index;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getLemma() {
        return lemma;
    }
}
