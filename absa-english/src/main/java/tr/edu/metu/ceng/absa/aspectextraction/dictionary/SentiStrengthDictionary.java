package tr.edu.metu.ceng.absa.aspectextraction.dictionary;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentiStrengthDictionary implements ISentimentDictionary {

    private static SentiStrengthDictionary sentiStrengthDictionary = null;

    private static Map<String, Double> dictionaryMap;
    private static String regex = "";
    private static Pattern pattern = null;

    static{
        try {
            initilize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static SentiStrengthDictionary getInstance(){
        if(sentiStrengthDictionary == null){
            sentiStrengthDictionary = new SentiStrengthDictionary();
        }
        return sentiStrengthDictionary;
    }

    private static void initilize() throws IOException {

        dictionaryMap = new HashMap<String, Double>();

        File file = new File("src\\main\\resources\\SentiStrengthEmotionLookupTable.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "UTF8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split("\t");
                splitted[0] = splitted[0].replace("*", ".*");
                Double score = null;
                if (splitted.length > 1) {
                    score = Double.parseDouble(splitted[1]);
                }
                dictionaryMap.put(splitted[0], score);
                regex += splitted[0].toLowerCase() + "|";
            }
            //System.out.println(regex);
            pattern = Pattern.compile(regex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean containsWord(final String word, final String pos){
        if (word == null) {
            return false;
        }
        return pattern.matcher(word.toLowerCase()).matches();
    }

    @Override
    public Double getScore(String word, String pos){
        Double score = 0.0;
        if (dictionaryMap.containsKey(word)) {
            score = dictionaryMap.get(word);
        } else {
            Pattern p = Pattern.compile(regex);

            Set<String> keys = dictionaryMap.keySet();
            Iterator<String> ite = keys.iterator();

            while (ite.hasNext()) {
                String candidate = ite.next();
                p = Pattern.compile(candidate);
                Matcher m = p.matcher(word.toLowerCase());
                if (m.matches()) {
                    score = dictionaryMap.get(candidate);
                    break;
                }
            }

        }

        return score;
    }
}
