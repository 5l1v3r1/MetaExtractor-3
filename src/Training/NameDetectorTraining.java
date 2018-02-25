package Training;

import opennlp.tools.namefind.*;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Collections;

public class NameDetectorTraining {

    public static void main(String[] args) {
        try {
            new NameDetectorTraining().Train();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Train() throws IOException {
        String onlpModelPath = "C:\\Users\\333da\\Desktop\\nlp\\en-person-custom-new0.bin";

        ObjectStream<NameSample> sampleStream =
                new NameSampleDataStream(
                        new PlainTextByLineStream(new MarkableFileInputStreamFactory(
                                new File("C:\\Users\\333da\\Desktop\\newnames0.txt")), "UTF-8"));
        TrainingParameters mlParams = new TrainingParameters();
        mlParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(70));
        mlParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(1));


        TokenNameFinderModel model = null;

        try {
            model = NameFinderME.train("en", "person", sampleStream, mlParams,
                    TokenNameFinderFactory.create(null, null, Collections.emptyMap(), new BioCodec()));
        } finally {
            sampleStream.close();
        }

        BufferedOutputStream modelOut = null;
        try {
            modelOut = new BufferedOutputStream(modelOut = new BufferedOutputStream(new FileOutputStream(onlpModelPath)));
            model.serialize(modelOut);
        } finally {
            if (modelOut != null)
                modelOut.close();
        }

    }
}
