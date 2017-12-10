package tr.edu.metu.ceng.absa.aspectextraction.pipeline;

import tr.edu.metu.ceng.absa.aspectextraction.dictionary.ISentimentDictionary;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Sentence;
import tr.edu.metu.ceng.absa.aspectextraction.entity.SentimentPhrase;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SentimentAnnotator implements ISentimentAnnotator {

    private ISentimentDictionary sentimentDictionary;

    private static final String EXCLUDED_LEXICON_ENTRIES_REGEX = "charge.*|amazon.*|smartphone.*";
    private static final Pattern EXCLUDED_LEXICON_ENTRIES_REGEX_PATTERN = Pattern.compile(SentimentAnnotator.EXCLUDED_LEXICON_ENTRIES_REGEX);


    public SentimentAnnotator(final ISentimentDictionary sentimentDictionary){

        this.sentimentDictionary = sentimentDictionary;
    }

    public boolean isExcludedEntry(final String word){
        if (word == null) {
            return false;
        }
        return EXCLUDED_LEXICON_ENTRIES_REGEX_PATTERN.matcher(word.toLowerCase()).matches();
    }

    public void annotateSentiments(Sentence sentence) {

        List<SentimentPhrase> sentimentPhrases = new ArrayList();


        final CoreMap coreMap = sentence.getCoreMap();

        CoreLabel previousToken;
        String previousTokensLemma = null;
        for (CoreLabel token : coreMap.get(CoreAnnotations.TokensAnnotation.class)) {
            String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);

            if(token.index() > 1){
                previousToken = coreMap.get(CoreAnnotations.TokensAnnotation.class).get(token.index() - 1 - 1);
                previousTokensLemma = previousToken.get(CoreAnnotations.LemmaAnnotation.class);
            }

            if("like".equalsIgnoreCase(word) && !pos.startsWith("VB")){
                continue;
            }

            if(isExcludedEntry(word)){
                continue;
            }

            if(sentimentDictionary.containsWord(word, pos) && !(NounPhraseAnnotator.isNoun(pos) || NounPhraseAnnotator.isProperNoun(pos))){
                double score = sentimentDictionary.getScore(word, pos);

                if("not".equalsIgnoreCase(previousTokensLemma)){
                    sentimentPhrases.add(new SentimentPhrase(word.toLowerCase(), pos, (-1) * score, token.index(), true));
                } else{
                    sentimentPhrases.add(new SentimentPhrase(word.toLowerCase(), pos, score, token.index()));
                }

            }

        }
        sentence.setSentimentPhrases(sentimentPhrases);

    }
}
