package tr.edu.metu.ceng.absa.aspectextraction.pipeline;

import tr.edu.metu.ceng.absa.aspectextraction.entity.*;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.matchfounder.INPAndSentimentMatchFounder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AspectSentimentMatchAnnotator implements IAspectSentimentMatchAnnotator {

    private INPAndSentimentMatchFounder matchFounder;

    public AspectSentimentMatchAnnotator(final INPAndSentimentMatchFounder matchFounder){
        this.matchFounder = matchFounder;
    }


    public void annotateMatches(Sentence sentence) {

        List<ISequenceElement> sequence = buildNounPhraseSentimentSequence(sentence);
        String sequenceAsString = convertToCharSequence(sequence);

        final List<Integer> matchStartIndices = matchFounder.findMatches(sequenceAsString);


        List<AspectSentimentMatch> aspectSentimentMatches = transformToAspectSentimentMatches(sentence, sequence, matchStartIndices);
        sentence.setAspectSentimentMatches(aspectSentimentMatches);


    }

    private List<AspectSentimentMatch> transformToAspectSentimentMatches(Sentence sentence, List<ISequenceElement> sequence, List<Integer> matchStartIndices) {


        //is NP an aspect or not?
        //if yes then create aspect from NP and create match and annotate in sentence

        //add all NN+ possibilities as aspect then filter them in excel manually, or even not

        //TODO
        /**
         * her matchi ekleme, NP aspect sözlüğünde varsa o matchi ekle o NP->Aspect dönüşümünü yapıp
         * false sentiment'ları çıkarmak mantıklı ki NPler doğru sentiment ile eşleşsin
         * ancak false aspectleri çıkarmamak lazım, false aspecti matchiyle beraber kaldırmak lazım;
         * çünkü en yakkın NP ve sentimentlar eşleşir tezimiz var.
         * aspectleri seç, matchleri filtrele
         *
         *
         *
         * koyduktan sonra sil, filtrele vtden vs (pozitif liste belirlenirken sondan 1,2,3 kelimeli NP'ler seçilebilmeli, bakılan listede yoksa )
         * matchleri filtreleyerek vtye yaz / koy (kod değişikliği)
         *
         *
         * seçilen aspectler totalde kaç reviewi kapsıyor, raporlamalı.
         *
         * total aspect sayısı / kaç aspectten kaç aspect seçileceği?
         */
        List<AspectSentimentMatch> matches = new ArrayList();
        for (Integer matchStartIndex : matchStartIndices) {

            final ISequenceElement sequenceElement = sequence.get(matchStartIndex);

            if(sequenceElement instanceof NounPhrase){
                final String lemma = ((NounPhrase) sequenceElement).getLemma();
                final SentimentPhrase sentimentPhrase = (SentimentPhrase) sequence.get(matchStartIndex + 1);

                addMatches(sentence, matches, lemma, sentimentPhrase);


            } else {
                final String lemma = ((NounPhrase) sequence.get(matchStartIndex + 1)).getLemma();
                final SentimentPhrase sentimentPhrase = (SentimentPhrase) sequenceElement;

                addMatches(sentence, matches, lemma, sentimentPhrase);
            }
        }
        return matches;
    }

    private void addMatches(Sentence sentence, List<AspectSentimentMatch> matches, String lemma, SentimentPhrase sentimentPhrase) {
        final String[] tokens = lemma.split(" ");

        //if NP has more than one word
        for(int i = tokens.length - 1; i >= 0; i--){
            StringBuilder sb = new StringBuilder();
            for(int j = i; j <= tokens.length - 1; j++){
                sb.append(tokens[j] + " ");
            }
            sb.deleteCharAt(sb.toString().length()-1);
            matches.add(new AspectSentimentMatch(sentence, new Aspect(sb.toString()), sentimentPhrase));
        }
    }

    private String convertToCharSequence(List<ISequenceElement> sequence) {
        StringBuilder sb = new StringBuilder();

        for (ISequenceElement sequenceElement : sequence) {
            sb.append(sequenceElement.getElementType());
        }
        return sb.toString();
    }

    private List<ISequenceElement> buildNounPhraseSentimentSequence(Sentence sentence) {

        //aspect and sentiment should be inserted in the returning list in an index ordered way
        final List<NounPhrase> nounPhrases = sentence.getNounPhrases();
        final List<SentimentPhrase> sentimentPhrases = sentence.getSentimentPhrases();

        List<ISequenceElement> wholeElements = new ArrayList();
        wholeElements.addAll(nounPhrases);
        wholeElements.addAll(sentimentPhrases);

        Collections.sort(wholeElements);
        return wholeElements;
    }

}
