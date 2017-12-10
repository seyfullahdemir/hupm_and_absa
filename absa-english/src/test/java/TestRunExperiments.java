import org.junit.Assert;
import org.junit.Test;
import tr.edu.metu.ceng.absa.aspectextraction.experiment.ExperimentRunner;
import tr.edu.metu.ceng.absa.aspectextraction.experiment.IExperimentRunner;
import tr.edu.metu.ceng.absa.aspectextraction.util.CSVFileWriter;
import tr.edu.metu.ceng.absa.common.datatypes.ABSAConstants;
import tr.edu.metu.ceng.absa.common.dbutil.SqlConnectionHelper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestRunExperiments {


    @Test
    public void testSampleReviewsWriteToCSVFile() throws IOException {

        final String inputFileName = "src\\main\\resources\\sample.json";
        final String outputFileName = "src\\main\\resources\\results.csv";
        IExperimentRunner experimentRunner =
                new ExperimentRunner(inputFileName, outputFileName);
        experimentRunner.run(new CSVFileWriter());
    }


    //only run consciously
    /*
    @Test
    public void testSampleReviewsWriteToDB() throws IOException {

        final String inputFileName = "src\\main\\resources\\sample.json";
        final String outputFileName = "src\\main\\resources\\results.csv";
        IExperimentRunner experimentRunner =
                new ExperimentRunner(inputFileName, outputFileName);
        experimentRunner.run(new DBWriter());
    }
*/

    @Test
    public void testAmazonReviewsWriteToCSVFile() throws IOException {

        final String inputFileName = "src\\main\\resources\\Cell_Phones_and_Accessories_5.json";
        final String outputFileName = "src\\main\\resources\\Cell_Phones_and_Accessories_5_Results.csv";
        IExperimentRunner experimentRunner =
                new ExperimentRunner(inputFileName, outputFileName);
        experimentRunner.run(new CSVFileWriter());
    }

    //only run consciously
    /*
    @Test
    public void testAmazonReviewsWriteToDB() throws IOException {

        final String inputFileName = "src\\main\\resources\\Cell_Phones_and_Accessories_5.json";
        final String outputFileName = "src\\main\\resources\\Cell_Phones_and_Accessories_5_Results.csv";
        IExperimentRunner experimentRunner =
                new ExperimentRunner(inputFileName, outputFileName);
        experimentRunner.run(new DBWriter());
    }
*/


    @Test
    public void testDBConnection() throws SQLException {
        SqlConnectionHelper sqlConn = new SqlConnectionHelper("localhost", ABSAConstants.DB_NAME, "root", "123456");
        final ResultSet resultSet = sqlConn.executeSelectQuery("select 1 as number from dual");
        if(resultSet.next()){
            Assert.assertEquals(1, resultSet.getInt("number"));
        } else{
            Assert.fail();
        }
    }
}
