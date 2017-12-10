package tr.edu.metu.ceng.absa.aspectextraction.dictionary;

import tr.edu.metu.ceng.absa.aspectextraction.entity.NounPhrase;

public class AspectDictionary implements IDictionary {


    public boolean containsEntry(String word) {
        return false;
    }


    /***
     * TODO
     * if NP is NN1 NN2 NN3
     * questions are
     *      is NN1 NN2 NN3 in aspect dictionary then NN1 NN2 NN3 is aspect, continue with that
     *      is NN2 NN3 in aspect dictionary then NN2 NN3 is aspect, continue with that
     *      is NN3 in aspect dictionary then NN3 is aspect, continue with that
     * */
    public boolean containsNounPhrase(NounPhrase nounPhrase) {
        return false;
    }
}
