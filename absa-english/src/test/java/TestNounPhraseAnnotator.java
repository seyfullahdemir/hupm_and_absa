import org.junit.Assert;
import org.junit.Test;
import tr.edu.metu.ceng.absa.aspectextraction.entity.NounPhrase;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Review;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Sentence;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.INLPAnnotator;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.INounPhraseAnnotator;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.NLPAnnotator;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.NounPhraseAnnotator;

import java.util.List;

public class TestNounPhraseAnnotator {



    @Test
    public void testPossessiveEndingCase() {

        INounPhraseAnnotator nounPhraseAnnotator = new NounPhraseAnnotator();

        final Sentence sentence = obtainSentence("I like telephone's voice");
        nounPhraseAnnotator.annotateNounPhrases(sentence);


        final List<NounPhrase> nounPhrases = sentence.getNounPhrases();
        Assert.assertEquals(1, nounPhrases.size());
        Assert.assertEquals("telephone's voice", nounPhrases.get(0).getWord());
        Assert.assertEquals("telephone voice", nounPhrases.get(0).getLemma());
    }


    @Test
    public void testNoNounPhrasesCase() {

        INounPhraseAnnotator nounPhraseAnnotator = new NounPhraseAnnotator();
        final Sentence sentence = obtainSentence("I do not like it");
        nounPhraseAnnotator.annotateNounPhrases(sentence);

        Assert.assertEquals(0, sentence.getNounPhrases().size());
    }


    @Test
    public void testMultipleNounPhrases() {

        INounPhraseAnnotator nounPhraseAnnotator = new NounPhraseAnnotator();

        final Sentence sentence = obtainSentence("I like the telephone screen and its keys, it is beautiful");
        nounPhraseAnnotator.annotateNounPhrases(sentence);

        final List<NounPhrase> nounPhrases = sentence.getNounPhrases();
        Assert.assertEquals(2, nounPhrases.size());
        Assert.assertEquals("telephone screen", nounPhrases.get(0).getWord());
        Assert.assertEquals("telephone screen", nounPhrases.get(0).getLemma());
        Assert.assertEquals("keys", nounPhrases.get(1).getWord());
        Assert.assertEquals("key", nounPhrases.get(1).getLemma());

    }



    @Test
    public void testProperNounsAreNotAnnotatedAsNounPhrase() {

        INounPhraseAnnotator nounPhraseAnnotator = new NounPhraseAnnotator();
        final Sentence sentence = obtainSentence("I like the Amazon company and also the Hamleys.");
        nounPhraseAnnotator.annotateNounPhrases(sentence);

        final List<NounPhrase> nounPhrases = sentence.getNounPhrases();
        Assert.assertEquals(1, nounPhrases.size());
        Assert.assertEquals("company", nounPhrases.get(0).getWord());
        Assert.assertNotEquals("Amazon", nounPhrases.get(0).getWord());
        Assert.assertNotEquals("Hamleys", nounPhrases.get(0).getWord());
    }


    @Test
    public void testMultipleNounPhrasesWithMultipleWordsCases() {

        INounPhraseAnnotator nounPhraseAnnotator = new NounPhraseAnnotator();

        final Sentence sentence = obtainSentence("I like the telephone screen telephone telephone mouse and phone key and computer then bottle table and water, it is beautiful");
        nounPhraseAnnotator.annotateNounPhrases(sentence);

        final List<NounPhrase> nounPhrases = sentence.getNounPhrases();
        Assert.assertEquals(5, nounPhrases.size());
        Assert.assertEquals("telephone screen telephone telephone mouse", nounPhrases.get(0).getWord());
        Assert.assertEquals("phone key", nounPhrases.get(1).getWord());
        Assert.assertEquals("computer", nounPhrases.get(2).getWord());
        Assert.assertEquals("bottle table", nounPhrases.get(3).getWord());
        Assert.assertEquals("water", nounPhrases.get(4).getWord());
    }


    @Test
    public void testMultipleNounPhrasesWithMultipleWordsAndAlsoWithPossesiveEndingCases() {

        INounPhraseAnnotator nounPhraseAnnotator = new NounPhraseAnnotator();

        final Sentence sentence = obtainSentence("I like the telephone screen's telephone telephone mouse and phone key and computer then bottle's table and water, it is beautiful");
        nounPhraseAnnotator.annotateNounPhrases(sentence);

        final List<NounPhrase> nounPhrases = sentence.getNounPhrases();
        Assert.assertEquals(5, nounPhrases.size());
        Assert.assertEquals("telephone screen telephone telephone mouse", nounPhrases.get(0).getLemma());
        Assert.assertEquals("phone key", nounPhrases.get(1).getLemma());
        Assert.assertEquals("computer", nounPhrases.get(2).getLemma());
        Assert.assertEquals("bottle table", nounPhrases.get(3).getLemma());
        Assert.assertEquals("water", nounPhrases.get(4).getLemma());
    }

    private Sentence obtainSentence(String reviewText) {
        INLPAnnotator nlpAnnotator = new NLPAnnotator();
        final List<Sentence> sentences = nlpAnnotator.annotateSentences(new Review(reviewText));

        return sentences.get(0);
    }

}
