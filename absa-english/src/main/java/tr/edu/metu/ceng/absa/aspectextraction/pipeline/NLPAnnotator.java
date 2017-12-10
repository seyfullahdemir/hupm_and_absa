package tr.edu.metu.ceng.absa.aspectextraction.pipeline;

import tr.edu.metu.ceng.absa.aspectextraction.entity.Review;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Sentence;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NLPAnnotator implements INLPAnnotator {
    public List<Sentence> annotateSentences(Review review) {

        List<Sentence> sentences = new ArrayList();

        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // read some text in the text variable
        String text = review.getReviewText();
        //String text = "It rolls up nice but has a hissing sound in the background. In the dead spot between two songs you can hear the hissing. It isnt all that loud but its there. Still works well enough that I didnt want to return it.";

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        final List<CoreMap> sentencesAsCoreMap = document.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentenceAsCoreMap : sentencesAsCoreMap) {
            Sentence sentence = new Sentence(review, sentenceAsCoreMap);
            sentences.add(sentence);
        }


        return sentences;
    }
}
