package tr.edu.metu.ceng.absa.aspectextraction.pipeline;

import tr.edu.metu.ceng.absa.aspectextraction.entity.NounPhrase;
import tr.edu.metu.ceng.absa.aspectextraction.entity.PartOfSpeech;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Sentence;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;

public class NounPhraseAnnotator implements INounPhraseAnnotator {

   //https://cs.nyu.edu/grishman/jet/guide/PennPOS.html

    /***
     * 	1.	CC	Coordinating conjunction
     2.	CD	Cardinal number
     3.	DT	Determiner
     4.	EX	Existential there
     5.	FW	Foreign word
     6.	IN	Preposition or subordinating conjunction
     7.	JJ	Adjective
     8.	JJR	Adjective, comparative
     9.	JJS	Adjective, superlative
     10.	LS	List item marker
     11.	MD	Modal
     12.	NN	Noun, singular or mass
     13.	NNS	Noun, plural
     14.	NNP	Proper noun, singular
     15.	NNPS	Proper noun, plural
     16.	PDT	Predeterminer
     17.	POS	Possessive ending
     18.	PRP	Personal pronoun
     19.	PRP$	Possessive pronoun
     20.	RB	Adverb
     21.	RBR	Adverb, comparative
     22.	RBS	Adverb, superlative
     23.	RP	Particle
     24.	SYM	Symbol
     25.	TO	to
     26.	UH	Interjection
     27.	VB	Verb, base form
     28.	VBD	Verb, past tense
     29.	VBG	Verb, gerund or present participle
     30.	VBN	Verb, past participle
     31.	VBP	Verb, non-3rd person singular present
     32.	VBZ	Verb, 3rd person singular present
     33.	WDT	Wh-determiner
     34.	WP	Wh-pronoun
     35.	WP$	Possessive wh-pronoun
     36.	WRB	Wh-adverb
     * @param sentence
     */


    public void annotateNounPhrases(Sentence sentence) {


        List<NounPhrase> nounPhrases = new ArrayList();

        final CoreMap coreMap = sentence.getCoreMap();
        CoreLabel previousToken;
        String previousTokensPOS = null;

        for (CoreLabel token: coreMap.get(CoreAnnotations.TokensAnnotation.class)) {
            String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);

            if(token.index() > 1){
                previousToken = coreMap.get(CoreAnnotations.TokensAnnotation.class).get(token.index() - 1 - 1);
                previousTokensPOS = previousToken.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            }

            if(isNoun(pos)){
                nounPhrases.add(new NounPhrase(word, lemma, token.index()));
            } else if(PartOfSpeech.POSSESSIVE_ENDING.getTag().equalsIgnoreCase(pos) && isNoun(previousTokensPOS)){
                final NounPhrase nounPhrase = nounPhrases.get(nounPhrases.size() - 1);
                nounPhrase.setWord(nounPhrase.getWord() + "'s");
            }

        }


        List<NounPhrase> mergedNounPhrases = new ArrayList();

        if(!nounPhrases.isEmpty()){
            final NounPhrase firstNounPhrase = nounPhrases.get(0);
            int startIndex = firstNounPhrase.getIndex();
            int lastIndex = firstNounPhrase.getIndex();
            StringBuilder wordSB = new StringBuilder();
            StringBuilder lemmaSB = new StringBuilder();

            wordSB.append(firstNounPhrase.getWord());
            lemmaSB.append(firstNounPhrase.getLemma());

            for(int i = 1; i < nounPhrases.size(); i++){
                final NounPhrase currentNounPhrase = nounPhrases.get(i);
                if(currentNounPhrase.getIndex() == lastIndex+1){
                    wordSB.append(" " + currentNounPhrase.getWord());
                    lemmaSB.append(" " + currentNounPhrase.getLemma());
                    lastIndex++;
                } else if(wordSB.toString().endsWith("'s") && currentNounPhrase.getIndex() == lastIndex+2){
                    wordSB.append(" " + currentNounPhrase.getWord());
                    lemmaSB.append(" " + currentNounPhrase.getLemma());
                    lastIndex+=2;
                } else {
                   mergedNounPhrases.add(new NounPhrase(wordSB.toString(), lemmaSB.toString(), startIndex));
                   lastIndex = currentNounPhrase.getIndex();
                   startIndex = lastIndex;

                   wordSB = new StringBuilder();
                   lemmaSB = new StringBuilder();
                   wordSB.append(currentNounPhrase.getWord());
                   lemmaSB.append(currentNounPhrase.getLemma());
                }
            }

            mergedNounPhrases.add(new NounPhrase(wordSB.toString(), lemmaSB.toString(), startIndex));


        }

        sentence.setNounPhrases(mergedNounPhrases);

    }

    public static boolean isNoun(String pos) {
        if(null == pos){
            return false;
        }
        return PartOfSpeech.NOUN.getTag().equalsIgnoreCase(pos) ||
                PartOfSpeech.NOUN_PLURAL.getTag().equalsIgnoreCase(pos);
    }

    public static boolean isProperNoun(String pos) {
        if(null == pos){
            return false;
        }
        return PartOfSpeech.NOUN_PROPER_SINGULAR.getTag().equalsIgnoreCase(pos) ||
                PartOfSpeech.NOUN_PROPER_PLURAL.getTag().equalsIgnoreCase(pos);
    }
}
