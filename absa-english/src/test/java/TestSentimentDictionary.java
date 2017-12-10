import org.junit.Assert;
import org.junit.Test;
import tr.edu.metu.ceng.absa.aspectextraction.dictionary.SentiStrengthDictionary;
import tr.edu.metu.ceng.absa.aspectextraction.dictionary.SentiWordNetDictionary;

public class TestSentimentDictionary {


    @Test
    public void testSentiWordNetDictionary() {

        SentiWordNetDictionary sentiWordNetDictionary = SentiWordNetDictionary.getInstance();
        Assert.assertTrue(sentiWordNetDictionary.containsWord("blue", "a"));
        System.out.println(sentiWordNetDictionary.getScore("blue", "a"));
    }


    @Test
    public void testSentiStrengthDictionary() {

        SentiStrengthDictionary sentiStrengthDictionary = SentiStrengthDictionary.getInstance();
        Assert.assertTrue(sentiStrengthDictionary.containsWord("bliss", null));
        Assert.assertTrue(sentiStrengthDictionary.containsWord("bLIss", null));
        Assert.assertEquals(Double.valueOf(5), sentiStrengthDictionary.getScore("bliss", null));
        Assert.assertEquals(Double.valueOf(5), sentiStrengthDictionary.getScore("bLIss", null));
        System.out.println(sentiStrengthDictionary.getScore("bliss" , null));
        System.out.println(sentiStrengthDictionary.getScore("bLIss" , null));
    }


    /***
     * Disadvantage: SentiStrength does not distinguish sentiment score for a sentiment word according to its different word senses.
     * Disadvantage: And we did not employ a Word Sense Disambiguation algorithm either.
     */


}
