import org.junit.Assert;
import org.junit.Test;
import tr.edu.metu.ceng.absa.aspectextraction.entity.AspectSentimentMatch;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Review;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.AspectSentimentMatchPipeline;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.IAspectSentimentMatchPipeline;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.matchfounder.INPAndSentimentMatchFounder;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.matchfounder.NPAndSentimentMatchFounder;

import java.util.List;

public class TestAspectSentimentMatchExtractor {

    @Test
    public void testNPAndSentimentMatchFounder(){
        INPAndSentimentMatchFounder matchFounder = new NPAndSentimentMatchFounder();

        List<Integer> matches = matchFounder.findMatches("SSN");
        Assert.assertTrue(matches.size() == 1);
        Assert.assertTrue(matches.contains(1));

        matches = matchFounder.findMatches("NSS");
        Assert.assertTrue(matches.size() == 1);
        Assert.assertTrue(matches.contains(0));


        matches = matchFounder.findMatches("NSN");
        Assert.assertTrue(matches.size() == 2);
        Assert.assertTrue(matches.contains(0));
        Assert.assertTrue(matches.contains(1));


        matches = matchFounder.findMatches("SNS");
        Assert.assertTrue(matches.size() == 2);
        Assert.assertTrue(matches.contains(0));
        Assert.assertTrue(matches.contains(1));



        matches = matchFounder.findMatches("NSNSNS");
        Assert.assertTrue(matches.size() == 5);
        Assert.assertTrue(matches.contains(0));
        Assert.assertTrue(matches.contains(1));
        Assert.assertTrue(matches.contains(2));
        Assert.assertTrue(matches.contains(3));
        Assert.assertTrue(matches.contains(4));


        matches = matchFounder.findMatches("SNSNSN");
        Assert.assertTrue(matches.size() == 5);
        Assert.assertTrue(matches.contains(0));
        Assert.assertTrue(matches.contains(1));
        Assert.assertTrue(matches.contains(2));
        Assert.assertTrue(matches.contains(3));
        Assert.assertTrue(matches.contains(4));



        matches = matchFounder.findMatches("NSSNSSNNSN");
        Assert.assertTrue(matches.size() == 6);
        Assert.assertTrue(matches.contains(0));
        Assert.assertTrue(matches.contains(2));
        Assert.assertTrue(matches.contains(3));
        Assert.assertTrue(matches.contains(5));
        Assert.assertTrue(matches.contains(7));
        Assert.assertTrue(matches.contains(8));

        matches = matchFounder.findMatches("NNNNNS");
        Assert.assertTrue(matches.size() == 1);
        Assert.assertTrue(matches.contains(4));



    }


    @Test
    public void testPrintMatches() {

        IAspectSentimentMatchPipeline extractor = new AspectSentimentMatchPipeline();
        final List<AspectSentimentMatch> aspectSentimentMatches = extractor.extractMatches(new Review("It rolls up nice but has a hissing sound in the background. In the dead spot between two songs you can hear the hissing. It isnt all that loud but its there. Still works well enough that I didnt want to return it."));
        for (AspectSentimentMatch aspectSentimentMatch : aspectSentimentMatches) {
            System.out.println(aspectSentimentMatch);
        }
    }

    @Test
    public void testNounPhraseIsMatchedBothOnePreviousAndOneNextSentiment() {

        IAspectSentimentMatchPipeline extractor = new AspectSentimentMatchPipeline();
        final List<AspectSentimentMatch> aspectSentimentMatches = extractor.extractMatches(new Review("I like the screen, it is beautiful"));

        Assert.assertEquals(2, aspectSentimentMatches.size());

        final AspectSentimentMatch firstMatch = aspectSentimentMatches.get(0);
        final AspectSentimentMatch secondMatch = aspectSentimentMatches.get(1);

        Assert.assertEquals("screen", firstMatch.getAspect().getWord());
        Assert.assertEquals("like", firstMatch.getSentimentPhrase().getWord());
        Assert.assertFalse(firstMatch.getSentimentPhrase().isNegated());
        Assert.assertEquals(Double.valueOf(2), Double.valueOf(firstMatch.getSentimentPhrase().getScore()));


        Assert.assertEquals("screen", secondMatch.getAspect().getWord());
        Assert.assertEquals("beautiful", secondMatch.getSentimentPhrase().getWord());
        Assert.assertFalse(secondMatch.getSentimentPhrase().isNegated());
        Assert.assertEquals(Double.valueOf(3), Double.valueOf(secondMatch.getSentimentPhrase().getScore()));
    }


    @Test
    public void testNegationWithOnePreviousWordNot() {

        IAspectSentimentMatchPipeline extractor = new AspectSentimentMatchPipeline();
        List<AspectSentimentMatch> aspectSentimentMatches = extractor.extractMatches(new Review("I don't like my job."));

        Assert.assertEquals(1, aspectSentimentMatches.size());
        AspectSentimentMatch aspectSentimentMatch = aspectSentimentMatches.get(0);

        Assert.assertEquals("job", aspectSentimentMatch.getAspect().getWord());
        Assert.assertEquals("like", aspectSentimentMatch.getSentimentPhrase().getWord());
        Assert.assertTrue(aspectSentimentMatch.getSentimentPhrase().isNegated());
        Assert.assertEquals(Double.valueOf(-2), Double.valueOf(aspectSentimentMatch.getSentimentPhrase().getScore()));

        aspectSentimentMatches = extractor.extractMatches(new Review("I like my job."));

        Assert.assertEquals(1, aspectSentimentMatches.size());
        aspectSentimentMatch = aspectSentimentMatches.get(0);
        Assert.assertEquals("job", aspectSentimentMatch.getAspect().getWord());
        Assert.assertEquals("like", aspectSentimentMatch.getSentimentPhrase().getWord());
        Assert.assertFalse(aspectSentimentMatch.getSentimentPhrase().isNegated());
        Assert.assertEquals(Double.valueOf(2), Double.valueOf(aspectSentimentMatch.getSentimentPhrase().getScore()));

        aspectSentimentMatches = extractor.extractMatches(new Review("I do not like my job."));

        Assert.assertEquals(1, aspectSentimentMatches.size());
        aspectSentimentMatch = aspectSentimentMatches.get(0);
        Assert.assertEquals("job", aspectSentimentMatch.getAspect().getWord());
        Assert.assertEquals("like", aspectSentimentMatch.getSentimentPhrase().getWord());
        Assert.assertTrue(aspectSentimentMatch.getSentimentPhrase().isNegated());
        Assert.assertEquals(Double.valueOf(-2), Double.valueOf(aspectSentimentMatch.getSentimentPhrase().getScore()));

        aspectSentimentMatches = extractor.extractMatches(new Review("Screen is not beautiful"));

        Assert.assertEquals(1, aspectSentimentMatches.size());
        aspectSentimentMatch = aspectSentimentMatches.get(0);
        Assert.assertEquals("screen", aspectSentimentMatch.getAspect().getWord());
        Assert.assertEquals("beautiful", aspectSentimentMatch.getSentimentPhrase().getWord());
        Assert.assertTrue(aspectSentimentMatch.getSentimentPhrase().isNegated());
        Assert.assertEquals(Double.valueOf(-3), Double.valueOf(aspectSentimentMatch.getSentimentPhrase().getScore()));


        aspectSentimentMatches = extractor.extractMatches(new Review("Screen is beautiful"));

        Assert.assertEquals(1, aspectSentimentMatches.size());
        aspectSentimentMatch = aspectSentimentMatches.get(0);
        Assert.assertEquals("screen", aspectSentimentMatch.getAspect().getWord());
        Assert.assertEquals("beautiful", aspectSentimentMatch.getSentimentPhrase().getWord());
        Assert.assertFalse(aspectSentimentMatch.getSentimentPhrase().isNegated());
        Assert.assertEquals(Double.valueOf(3), Double.valueOf(aspectSentimentMatch.getSentimentPhrase().getScore()));


        aspectSentimentMatches = extractor.extractMatches(new Review("Screen isn't beautiful"));

        Assert.assertEquals(1, aspectSentimentMatches.size());
        aspectSentimentMatch = aspectSentimentMatches.get(0);
        Assert.assertEquals("screen", aspectSentimentMatch.getAspect().getWord());
        Assert.assertEquals("beautiful", aspectSentimentMatch.getSentimentPhrase().getWord());
        Assert.assertTrue(aspectSentimentMatch.getSentimentPhrase().isNegated());
        Assert.assertEquals(Double.valueOf(-3), Double.valueOf(aspectSentimentMatch.getSentimentPhrase().getScore()));
    }

    @Test
    public void testNonverbalLikeIsNotAnnotatedAsSentimentWord() {

        IAspectSentimentMatchPipeline extractor = new AspectSentimentMatchPipeline();
        final List<AspectSentimentMatch> aspectSentimentMatches = extractor.extractMatches(new Review("The picture is like an awesome collage."));

        Assert.assertEquals(2, aspectSentimentMatches.size());
        final AspectSentimentMatch firstMatch = aspectSentimentMatches.get(0);
        final AspectSentimentMatch secondMatch = aspectSentimentMatches.get(1);

        Assert.assertEquals("picture", firstMatch.getAspect().getWord());
        Assert.assertEquals("awesome", firstMatch.getSentimentPhrase().getWord());
        Assert.assertNotEquals("like", firstMatch.getSentimentPhrase().getWord());
        Assert.assertFalse(firstMatch.getSentimentPhrase().isNegated());
        Assert.assertEquals(Double.valueOf(3), Double.valueOf(firstMatch.getSentimentPhrase().getScore()));

        Assert.assertEquals("collage", secondMatch.getAspect().getWord());
        Assert.assertEquals("awesome", secondMatch.getSentimentPhrase().getWord());
        Assert.assertNotEquals("like", secondMatch.getSentimentPhrase().getWord());
        Assert.assertFalse(secondMatch.getSentimentPhrase().isNegated());
        Assert.assertEquals(Double.valueOf(3), Double.valueOf(secondMatch.getSentimentPhrase().getScore()));
    }


    @Test
    public void testNonVerbalLikeCaseWithAlsoOneVerbalLikeCaseExists(){
        final String sentence = "This is the kind of screen like the monitor that I like";

        IAspectSentimentMatchPipeline extractor = new AspectSentimentMatchPipeline();
        final List<AspectSentimentMatch> aspectSentimentMatches = extractor.extractMatches(new Review(sentence));

        Assert.assertEquals(1, aspectSentimentMatches.size());
        final AspectSentimentMatch firstMatch = aspectSentimentMatches.get(0);
        Assert.assertEquals("monitor", firstMatch.getAspect().getWord());
        Assert.assertEquals("like", firstMatch.getSentimentPhrase().getWord());


    }
    @Test
    public void testForMultipleWordNounPhrasesMultipleFormCombinationsOfNounPhrasesConvertedToPossibleAspects(){
        final String sentence = "I like the telephone's screen keys";

        IAspectSentimentMatchPipeline extractor = new AspectSentimentMatchPipeline();
        final List<AspectSentimentMatch> aspectSentimentMatches = extractor.extractMatches(new Review(sentence));

        Assert.assertEquals(3, aspectSentimentMatches.size());
        final AspectSentimentMatch firstMatch = aspectSentimentMatches.get(0);
        final AspectSentimentMatch secondMatch = aspectSentimentMatches.get(1);
        final AspectSentimentMatch thirdMatch = aspectSentimentMatches.get(2);

        Assert.assertEquals("like", firstMatch.getSentimentPhrase().getWord());
        Assert.assertEquals("like", secondMatch.getSentimentPhrase().getWord());
        Assert.assertEquals("like", thirdMatch.getSentimentPhrase().getWord());


        Assert.assertEquals("key", firstMatch.getAspect().getWord());
        Assert.assertEquals("screen key", secondMatch.getAspect().getWord());
        Assert.assertEquals("telephone screen key", thirdMatch.getAspect().getWord());

    }

    /**
     Varsayım: Her Noun Phrase kendisinden "bir önceki" ve/veya "bir sonraki" (arada başka bir Noun Phrase olmadan) Sentiment Word kelimesi ile ilişkilidir,
     Varsayım: Her Sentiment Word kendisinden varsa "bir önceki" ve/veya "bir sonraki" (arada başka bir Sentiment Word olmadan) Noun Phrase kelimesi ile ilişkilidir,
     False Match (Precision'ı Bozan): Aslında ilişkili olmayan komşu iki "Sentiment Word" ve  "Noun Phrase" birbiriyle eşleştirilmiş olabilir.
     Eksik Match 1 (Recall'u Bozan): Bir birim uzaklıktan daha uzun bir contextte ilişkili olan eşleşmeler çıkarılamamış olabilir. Hatta yine birden fazla cümleye yayılan contextlerdeki eşleşmeler çıkarılamaz.
     Eksik Match 2 (Recall'u Bozan): Birden fazla kelimeden oluşan Noun Phrase'lerin eşleştirmelere yansıtılma yönteminden kaynaklı eksik eşleşmeler de olabilir.
     Örneğin: "Screen keyboard" Noun Phrase'i için "Screen Keyboard" ve "Keyboard" olarak komşu Sentiment'larla eşletirilmesini sağlıyoruz. "Screen Keyboard"la ilgili bir Sentiment duruma göre "Screen" Aspectini de bağlıyor olabilir (ve benzer diğer Noun Phrase'ler için de aynı şekilde), bu da eksik match'e sebep olabilir.  Ama bir yandan örneğin; "plastic screen protector" Noun Phrase'i için "screen" bir Aspect değildir. "screen protector" / "protector" / "plastic screen protector" bir Aspecttir.
     */

    /**
     * Bağlaçlarla bağlanmış Noun Phrase'ler ve yine bağlaçlarla bağlanmış Sentiment Word'lerde bağlacın gerektirdiği anlamsal detayı eşleşmelere / sentiment skorlarına yansıtamıyoruz.
     */

}
