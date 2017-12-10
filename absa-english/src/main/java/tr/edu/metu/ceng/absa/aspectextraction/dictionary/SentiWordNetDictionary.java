package tr.edu.metu.ceng.absa.aspectextraction.dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SentiWordNetDictionary implements ISentimentDictionary{

    private static SentiWordNetDictionary sentiWordNetDictionary = null;
    private static Map<String, Double> dictionaryMap;

    static{
        try {
            initilize();
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }


    public static SentiWordNetDictionary getInstance(){
        if(sentiWordNetDictionary == null){
            sentiWordNetDictionary = new SentiWordNetDictionary();
        }
        return sentiWordNetDictionary;
    }

    /**
     * http://sentiwordnet.isti.cnr.it/code/SentiWordNetDemoCode.java
     *
     *Here you can find a demo java class, kindly provided by Petter Tönberg, that uses SentiWordNet to approximate the sentiment value of a word with a label.

     P.S. Note that in this piece of code there is no word sense disambiguation and the scores are approximated with labels, so it's very basic.
     *
     * @throws IOException
     */
    private static void initilize() throws IOException {
        // This is our main dictionary representation
        dictionaryMap = new HashMap<String, Double>();

        // From String to list of doubles.
        HashMap<String, HashMap<Integer, Double>> tempDictionary = new HashMap<String, HashMap<Integer, Double>>();

        BufferedReader csv = null;
        try {
            csv = new BufferedReader(new FileReader("src\\main\\resources\\SentiWordNet_3.0.0_20130122.txt"));
            int lineNumber = 0;

            String line;
            while ((line = csv.readLine()) != null) {
                lineNumber++;

                // If it's a comment, skip this line.
                if (!line.trim().startsWith("#")) {
                    // We use tab separation
                    String[] data = line.split("\t");
                    String wordTypeMarker = data[0];

                    // Example line:
                    // POS ID PosS NegS SynsetTerm#sensenumber Desc
                    // a 00009618 0.5 0.25 spartan#4 austere#3 ascetical#2
                    // ascetic#2 practicing great self-denial;...etc

                    // Is it a valid line? Otherwise, through exception.
                    if (data.length != 6) {
                        throw new IllegalArgumentException(
                                "Incorrect tabulation format in file, line: "
                                        + lineNumber);
                    }

                    // Calculate synset score as score = PosS - NegS
                    Double synsetScore = Double.parseDouble(data[2])
                            - Double.parseDouble(data[3]);

                    // Get all Synset terms
                    String[] synTermsSplit = data[4].split(" ");

                    // Go through all terms of current synset.
                    for (String synTermSplit : synTermsSplit) {
                        // Get synterm and synterm rank
                        String[] synTermAndRank = synTermSplit.split("#");
                        String synTerm = synTermAndRank[0] + "#"
                                + wordTypeMarker;

                        int synTermRank = Integer.parseInt(synTermAndRank[1]);
                        // What we get here is a map of the type:
                        // term -> {score of synset#1, score of synset#2...}

                        // Add map to term if it doesn't have one
                        if (!tempDictionary.containsKey(synTerm)) {
                            tempDictionary.put(synTerm,
                                    new HashMap<Integer, Double>());
                        }

                        // Add synset link to synterm
                        tempDictionary.get(synTerm).put(synTermRank,
                                synsetScore);
                    }
                }
            }

            // Go through all the terms.
            for (Map.Entry<String, HashMap<Integer, Double>> entry : tempDictionary
                    .entrySet()) {
                String word = entry.getKey();
                Map<Integer, Double> synSetScoreMap = entry.getValue();

                // Calculate weighted average. Weigh the synsets according to
                // their rank.
                // Score= 1/2*first + 1/3*second + 1/4*third ..... etc.
                // Sum = 1/1 + 1/2 + 1/3 ...
                double score = 0.0;
                double sum = 0.0;
                for (Map.Entry<Integer, Double> setScore : synSetScoreMap
                        .entrySet()) {
                    score += setScore.getValue() / (double) setScore.getKey();
                    sum += 1.0 / (double) setScore.getKey();
                }
                score /= sum;

                dictionaryMap.put(word, score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csv != null) {
                csv.close();
            }
        }
    }

    private SentiWordNetDictionary(){

    }



    @Override
    public boolean containsWord(String word, String pos) {
        //TODO
        //for sentiwordnet JJ-a, V*-v, NN-n, RB-r
        return dictionaryMap.containsKey(word + "#"  + pos);
    }

    //no word sense disambiguation
    @Override
    public Double getScore(String word, String pos) {

        //TODO
        //for sentiwordnet JJ-a, V*-v, NN-n, RB-r
        return dictionaryMap.get(word + "#"  + pos);
    }
}
