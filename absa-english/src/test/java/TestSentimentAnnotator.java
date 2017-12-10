import org.junit.Assert;
import org.junit.Test;
import tr.edu.metu.ceng.absa.aspectextraction.dictionary.SentiStrengthDictionary;
import tr.edu.metu.ceng.absa.aspectextraction.entity.NounPhrase;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Review;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Sentence;
import tr.edu.metu.ceng.absa.aspectextraction.entity.SentimentPhrase;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.*;

import java.util.List;

public class TestSentimentAnnotator {


    @Test
    public void testExcludedWordsAreNotAnnotatedAsSentimentWord() {

        //exluded words are "charge.*|amazon.*|smartphone.*"

        ISentimentAnnotator sentimentAnnotator = new SentimentAnnotator(new SentiStrengthDictionary());

        Sentence sentence = obtainSentence("The charger charges well");
        sentimentAnnotator.annotateSentiments(sentence);

        List<SentimentPhrase> sentimentPhrases = sentence.getSentimentPhrases();

        //charge and well are in both SentiStrengthDictionary
        Assert.assertEquals(1, sentimentPhrases.size());
        SentimentPhrase sentimentPhrase = sentimentPhrases.get(0);
        Assert.assertEquals("well", sentimentPhrase.getWord());
        Assert.assertNotEquals("charges", sentimentPhrase.getWord());


        sentence = obtainSentence("I like Amazon and Smartphones and smartpads.");
        sentimentAnnotator.annotateSentiments(sentence);

        sentimentPhrases = sentence.getSentimentPhrases();
        Assert.assertEquals(1, sentimentPhrases.size());
        sentimentPhrase = sentimentPhrases.get(0);

        //charge and well are in both SentiStrengthDictionary
        Assert.assertEquals(1, sentimentPhrases.size());
        Assert.assertEquals("like", sentimentPhrase.getWord());


    }


    @Test
    public void testNonVerbalLikeIsNotAnnotatedAsSentimentWordButVerbalOnesAre(){
        ISentimentAnnotator sentimentAnnotator = new SentimentAnnotator(new SentiStrengthDictionary());

        Sentence sentence = obtainSentence("This is the kind of screen like the monitor that I love");
        sentimentAnnotator.annotateSentiments(sentence);

        List<SentimentPhrase> sentimentPhrases = sentence.getSentimentPhrases();
        Assert.assertEquals(1, sentimentPhrases.size());

        SentimentPhrase sentimentPhrase = sentimentPhrases.get(0);

        Assert.assertEquals("love", sentimentPhrase.getWord());
        Assert.assertNotEquals("like", sentimentPhrase.getWord());


        sentence = obtainSentence("This is the monitor that I like");
        sentimentAnnotator.annotateSentiments(sentence);

        sentimentPhrases = sentence.getSentimentPhrases();
        Assert.assertEquals(1, sentimentPhrases.size());

        sentimentPhrase = sentimentPhrases.get(0);

        Assert.assertEquals("like", sentimentPhrase.getWord());


    }

    @Test
    public void testIfTheCaseIsNotImportantForSentimentWord() {

        ISentimentAnnotator sentimentAnnotator = new SentimentAnnotator(new SentiStrengthDictionary());

        Sentence sentence = obtainSentence("I love it and I LIKE it and it so WeLl");
        sentimentAnnotator.annotateSentiments(sentence);

        List<SentimentPhrase> sentimentPhrases = sentence.getSentimentPhrases();
        Assert.assertEquals(3, sentimentPhrases.size());

        Assert.assertEquals("love", sentimentPhrases.get(0).getWord());
        Assert.assertEquals("like", sentimentPhrases.get(1).getWord());
        Assert.assertEquals("well", sentimentPhrases.get(2).getWord());
    }


    @Test
    public void testEvenItExistsInDictionaryNounsAndProperNounsAreNotAnnotatedAsSentimentWords(){
        ISentimentAnnotator sentimentAnnotator = new SentimentAnnotator(new SentiStrengthDictionary());
        INounPhraseAnnotator nounPhraseAnnotator = new NounPhraseAnnotator();

        Sentence sentence = obtainSentence("I like the boredom and Amazon company.");
        sentimentAnnotator.annotateSentiments(sentence);
        nounPhraseAnnotator.annotateNounPhrases(sentence);

        List<SentimentPhrase> sentimentPhrases = sentence.getSentimentPhrases();
        final List<NounPhrase> nounPhrases = sentence.getNounPhrases();

        Assert.assertEquals(2, nounPhrases.size());
        Assert.assertEquals("boredom", nounPhrases.get(0).getWord());
        Assert.assertEquals("company", nounPhrases.get(1).getWord());

        Assert.assertEquals(1, sentimentPhrases.size());
        Assert.assertEquals("like", sentimentPhrases.get(0).getWord());
        Assert.assertNotEquals("boredom", sentimentPhrases.get(0).getWord());
        Assert.assertNotEquals("Amazon", sentimentPhrases.get(0).getWord());

    }


    @Test
    public void testNegationWithOnePreviousWordNot(){

        assertAnnotatedSentiment("I don't like my job.", "like", true, Double.valueOf(-2));
        assertAnnotatedSentiment("I like my job.", "like", false, Double.valueOf(2));
        assertAnnotatedSentiment("I do not like my job.", "like", true, Double.valueOf(-2));
        assertAnnotatedSentiment("Screen is not beautiful", "beautiful", true, Double.valueOf(-3));
        assertAnnotatedSentiment("Screen is beautiful", "beautiful", false, Double.valueOf(3));
        assertAnnotatedSentiment("Screen isn't beautiful", "beautiful", true, Double.valueOf(-3));

        /**
         *
         * Some Notes
         * 1. Actually, sometimes negation does not negate the sentiment to the exact opposite way
         * For example,
         * "not perfect"  : must be still positive
         * "don't hate but" : must be still negative
         *
         * Our method misses this detail.
         *
         *
         * 2. Also, we did not take into account of boosting or enhancing sentiment words like; quite expensive, little difficult, highly beautiful
         *
         * 3. We only consider the negation that is made with the previos not word; some other possibilites that we are missing:
         *    * negations made within longer contexts like "It does not even have good battery"
         */

    }

    private void assertAnnotatedSentiment(final String review, final String expectedSentimentWord, boolean expectedIsNegated, final Double expectedSentimentScore) {
        ISentimentAnnotator sentimentAnnotator = new SentimentAnnotator(new SentiStrengthDictionary());

        Sentence sentence = obtainSentence(review);
        sentimentAnnotator.annotateSentiments(sentence);
        final List<SentimentPhrase> sentimentPhrases = sentence.getSentimentPhrases();
        Assert.assertEquals(1, sentimentPhrases.size());

        Assert.assertEquals(expectedIsNegated, sentimentPhrases.get(0).isNegated());
        Assert.assertEquals(expectedSentimentWord, sentimentPhrases.get(0).getWord());
        Assert.assertEquals(expectedSentimentScore, Double.valueOf(sentimentPhrases.get(0).getScore()));
    }

    private Sentence obtainSentence(String reviewText) {
        INLPAnnotator nlpAnnotator = new NLPAnnotator();
        final List<Sentence> sentences = nlpAnnotator.annotateSentences(new Review(reviewText));

        return sentences.get(0);
    }
}
