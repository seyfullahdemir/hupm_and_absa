package tr.edu.metu.ceng.absa.aspectextraction.pipeline;

import tr.edu.metu.ceng.absa.aspectextraction.entity.AspectSentimentMatch;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Review;
import tr.edu.metu.ceng.absa.aspectextraction.entity.Sentence;
import tr.edu.metu.ceng.absa.aspectextraction.dictionary.SentiStrengthDictionary;
import tr.edu.metu.ceng.absa.aspectextraction.pipeline.matchfounder.NPAndSentimentMatchFounder;

import java.util.ArrayList;
import java.util.List;

public class AspectSentimentMatchPipeline implements IAspectSentimentMatchPipeline {

    private INLPAnnotator nlpAnnotator;
    private INounPhraseAnnotator nounPhraseAnnotator;
    private ISentimentAnnotator sentimentAnnotator;
    private IAspectSentimentMatchAnnotator aspectSentimentMatchAnnotator;

    public AspectSentimentMatchPipeline(){
        nlpAnnotator = new NLPAnnotator();
        nounPhraseAnnotator = new NounPhraseAnnotator();
        sentimentAnnotator = new SentimentAnnotator(SentiStrengthDictionary.getInstance());
        aspectSentimentMatchAnnotator = new AspectSentimentMatchAnnotator(new NPAndSentimentMatchFounder());
    }


    public List<AspectSentimentMatch> extractMatches(Review review) {
        List<AspectSentimentMatch> aspectSentimentMatches = new ArrayList();

        List<Sentence> sentences = nlpAnnotator.annotateSentences(review);

        /***
         * Batuhan'ın çalışmasına göre farklılıklar
         *
         * noun phrase çıkartırken conjunction ile bağlanan NPleri tek NP olarak almışlar, biz almadık.
         * sentimentlarda da aynı şekilde
         * eşleşme için diğerine göre daha çok geçen grup hangisiyse onun az geçenin sayısı kadar kombinasyonu alıp,
         *  hangi kombinasyon için sırası aynı indis sırasındaki NP ve SP arası distance'ları toplamı minimumsa o seçilmiş.
         *
         *  bizde ise sadece yanyana geçenler match olarak kabul ediliyor.
         *
         *  NP to aspect çevrimi:
         *  Batuhan'ın çalışmasında tek bir NP SP eşleşmesinden Aspect Count * Sentiment Word Count sayısı kadar eşleşme oluşturuluyor.
         *  Batuhan and ile bağlı NPleri saklıyor
         *  sentiment wordleri grup olarak saklıyor, arada bağlaç tutmuyor. eşleştiği aspect için grubun her elemanıyla bir match ekliyor.
         *
         *  NP lookup farkı
         *  NPde kelime grubunun başını sabit tutup sondan kırparak gelmişler
         *  biz sonunu tutup başını kırparak gidiyoruz.
         *  Asıl aspect sonda olur motivasyonu ile
         */
        for (Sentence sentence : sentences) {
            nounPhraseAnnotator.annotateNounPhrases(sentence);
            sentimentAnnotator.annotateSentiments(sentence);
            aspectSentimentMatchAnnotator.annotateMatches(sentence);

            List<AspectSentimentMatch> matches = sentence.getAspectSentimentMatches();

            aspectSentimentMatches.addAll(matches);

        }

        return aspectSentimentMatches;
    }
}
