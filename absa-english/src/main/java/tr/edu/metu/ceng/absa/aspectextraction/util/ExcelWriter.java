package tr.edu.metu.ceng.absa.aspectextraction.util;

import tr.edu.metu.ceng.absa.aspectextraction.entity.AspectSentimentMatch;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelWriter implements IFileWriter {
    @Override
    public void writeResults(String filePath, List<AspectSentimentMatch> aspectSentimentMatches) throws IOException {

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();


        writeHeading(sheet);
        int rowCount = 1;
        for (AspectSentimentMatch match : aspectSentimentMatches) {
            Row row = sheet.createRow(rowCount++);
            writeMatch(match, row);
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }
    }

    private void writeHeading(Sheet sheet) {
        Row row = sheet.createRow(0);

        Cell cell = row.createCell(0);
        cell.setCellValue("REVIEW ID");

        cell = row.createCell(1);
        cell.setCellValue("SENTENCE");

        cell = row.createCell(2);
        cell.setCellValue("ASPECT");

        cell = row.createCell(3);
        cell.setCellValue("SENTIMENT");

        cell = row.createCell(4);
        cell.setCellValue("SENTIMENT_SCORE");
    }

    private void writeMatch(AspectSentimentMatch match, Row row) {
        Cell cell = row.createCell(0);
        cell.setCellValue(match.getSentence().getReview().getReviewId());

        cell = row.createCell(1);
        cell.setCellValue(match.getSentence().getSentenceText());

        cell = row.createCell(2);
        cell.setCellValue(match.getAspect().getWord());

        cell = row.createCell(3);
        cell.setCellValue(match.getSentimentPhrase().getWord());

        cell = row.createCell(4);
        cell.setCellValue(match.getSentimentPhrase().getScore());
    }
}
