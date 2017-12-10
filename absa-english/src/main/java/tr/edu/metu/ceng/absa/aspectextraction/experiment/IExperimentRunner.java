package tr.edu.metu.ceng.absa.aspectextraction.experiment;

import tr.edu.metu.ceng.absa.aspectextraction.util.IFileWriter;

import java.io.IOException;

public interface IExperimentRunner {
    void run(IFileWriter fileWriter) throws IOException;
}
