package tr.edu.metu.ceng.ubim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AspectsUtil {

    public String createCommaSeperatedAspects() throws IOException {
        final String inputFileName = "aspects.txt";
        final List<String> aspects = readAspects(inputFileName);
        StringBuilder sb = new StringBuilder();

        for (String aspect : aspects) {
            sb.append("'" + aspect + "',");
        }
        sb.deleteCharAt(sb.toString().length()-1);

        String commaSeperatedAspects = sb.toString();
        return commaSeperatedAspects;
    }


    protected List<String> readAspects(String inputFileName) throws IOException {
        FileReader in = new FileReader(inputFileName);
        BufferedReader br = new BufferedReader(in);

        List<String> aspects = new ArrayList();
        String line;
        while ((line = br.readLine()) != null) {
            aspects.add(line);
        }
        in.close();
        return aspects;
    }

}
