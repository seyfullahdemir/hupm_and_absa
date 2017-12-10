import org.junit.Assert;
import org.junit.Test;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Review;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Sentence;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.INLPAnnotator;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.NLPAnnotator;

import java.util.List;

public class TestNLPAnnotator {


    @Test
    public void testSegmentateSentences(){
        INLPAnnotator nlpAnnotator = new NLPAnnotator();
        final String firstSentence = "This is the first sentence.";
        final String secondSentence = "And this is the second.";
        final List<Sentence> sentences = nlpAnnotator.annotateSentences(new Review(firstSentence + " " + secondSentence));
        Assert.assertEquals(2, sentences.size());
        Assert.assertEquals(firstSentence, sentences.get(0).getSentenceText());
        Assert.assertEquals(secondSentence, sentences.get(1).getSentenceText());
    }

    ///known failure case
    ///ttps://stackoverflow.com/questions/44858741/nltk-tokenizer-and-stanford-corenlp-tokenizer-cannot-distinct-2-sentences-withou
    @Test
    public void testSegmentateSentencesWithoutSpaceBetweenSentences(){
        INLPAnnotator nlpAnnotator = new NLPAnnotator();
        final String firstSentence = "This is the first sentence.";
        final String secondSentence = "And this is the second.";
        final List<Sentence> sentences = nlpAnnotator.annotateSentences(new Review(firstSentence + secondSentence));
        Assert.assertEquals(2, sentences.size());
        Assert.assertEquals(firstSentence, sentences.get(0).getSentenceText());
        Assert.assertEquals(secondSentence, sentences.get(1).getSentenceText());
    }

}
