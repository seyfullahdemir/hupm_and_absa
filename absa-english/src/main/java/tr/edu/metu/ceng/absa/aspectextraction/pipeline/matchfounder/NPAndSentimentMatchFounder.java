package tr.edu.metu.ceng.absa.aspectextraction.pipeline.matchfounder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NPAndSentimentMatchFounder implements INPAndSentimentMatchFounder {
    @Override
    public List<Integer> findMatches(String sequenceAsString) {

        Matcher m = Pattern.compile("(?=(NS|SN))").matcher(sequenceAsString);
        List<Integer> pos = new ArrayList<Integer>();
        while (m.find())
        {
            pos.add(m.start());
        }

        return pos;

    }
}
