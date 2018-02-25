import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.FileInputStream;
import java.io.IOException;

public class SentenceSplitter {
    public SentenceSplitter() {}

    public String[] split(String fileStr) {
        SentenceDetectorME sentenceDetector = null;
        FileInputStream modelIn = null;

        try {
            modelIn = new FileInputStream("C:\\Users\\333da\\Desktop\\nlp\\en-sent.bin");
            SentenceModel sentenceModel = new SentenceModel(modelIn);
            modelIn.close();
            sentenceDetector = new SentenceDetectorME(sentenceModel);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (final IOException e) {}
            }
        }
        String sentences[]=(sentenceDetector.sentDetect(fileStr));


        for(int i = 0; i < sentences.length; i++) {
            if (i > 0 && sentences[i-1].length() < 10) {
                sentences[i-1] += sentences[i];

                int removeIndex = i;

                for(int j = removeIndex; j < sentences.length - 1; j++){
                    sentences[i] = sentences[i + 1];
                }
            }
        }
        //for(int i = 0; i < sentences.length; i++) {
        //    System.out.println(sentences[i]);
        //}
        return sentences;
    }
}
