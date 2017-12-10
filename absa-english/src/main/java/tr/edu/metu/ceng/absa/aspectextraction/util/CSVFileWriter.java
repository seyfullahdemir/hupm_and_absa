package tr.edu.metu.ceng.absa.aspectextraction.util;

import tr.edu.metu.ceng.absa.aspectextraction.entity.AspectSentimentMatch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVFileWriter implements IFileWriter {

    //Delimiter used in CSV file
    private static final String DELIMITER = "|||";
    private static final String NEW_LINE_SEPARATOR = "\n";

    //CSV file header
    private static final String FILE_HEADER = "REVIEW_ID" + DELIMITER + "SENTENCE" + DELIMITER + "ASPECT" + DELIMITER+ "SENTIMENT" + DELIMITER + "SENTIMENT_SCORE";

    @Override
    public void writeResults(String filePath, List<AspectSentimentMatch> aspectSentimentMatches) throws IOException {

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(filePath);

            //Write the CSV file header
            fileWriter.append(FILE_HEADER.toString());

            //Add a new line separator after the header
            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write match to the CSV file
            for (AspectSentimentMatch match : aspectSentimentMatches) {
                fileWriter.append(String.valueOf(match.getSentence().getReview().getReviewId()));
                fileWriter.append(DELIMITER);
                fileWriter.append(match.getSentence().getSentenceText());
                fileWriter.append(DELIMITER);
                fileWriter.append(match.getAspect().getWord());
                fileWriter.append(DELIMITER);
                fileWriter.append(match.getSentimentPhrase().getWord());
                fileWriter.append(DELIMITER);
                fileWriter.append(String.valueOf(match.getSentimentPhrase().getScore()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }



            System.out.println("CSV file was created successfully !!!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }

        }
    }
}
