import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import ExtractFacts.Extractor;
import Struct.Name;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class Main {
    public void Main() {}

    private static long selected=0;
    private static long target=0;
    private static long truePositive=0;

    public static double getPrecisionScore() {
        return selected > 0 ? (double) truePositive / (double) selected : 0;
    }

    public static double getRecallScore() {
        return target > 0 ? (double) truePositive / (double) target : 0;
    }

    public static void updateScores(final List<String> references, final List<String> predictions) {

        truePositive += countTruePositives(references, predictions);
        selected += predictions.size();
        target += references.size();
    }
    static int countTruePositives(final List<String> references, final List<String> predictions) {
        int truePositives = 0;
        Object matchedItem = null;

        for (String referenceName : references) {
            for (String predListSpan :predictions) {
                if (referenceName.equals(predListSpan)) {
                    matchedItem = predListSpan;
                    truePositives++;
                }
            }
            if (matchedItem != null) {
                //predListSpans.remove(matchedItem);
            }
        }
        return truePositives;
    }
    public static double precision(final List<String> references, final List<String> predictions) {

        if (predictions.size() > 0) {
            return countTruePositives(references, predictions)
                    / (double) predictions.size();
        } else {
            return Double.NaN;
        }
    }

    public static double recall(final List<String> references, final List<String> predictions) {

        if (references.size() > 0) {
            return countTruePositives(references, predictions)
                    / (double) references.size();
        } else {
            return Double.NaN;
        }
    }

    public static double getFMeasure() {

        if (getPrecisionScore() + getRecallScore() > 0) {
            return 2 * (getPrecisionScore() * getRecallScore())
                    / (getPrecisionScore() + getRecallScore());
        } else {
            // cannot divide by zero, return error code
            return -1;
        }
    }

    private static List<String> displayNames(Span[] names, double[] spanProbs, String[] tokens) throws FileNotFoundException {
        //System.out.print("NAMES\n");

        PrintWriter out = new PrintWriter(new FileOutputStream("resources\\out\\names.txt", true));

        List<Name> namesList = new ArrayList<Name>();

        List<String> namesGet = new ArrayList<String>();

        for (int si = 0; si < names.length; si++) { //<co id="co.opennlp.name.eachname"/>
            //System.out.println("Span: "+names[si].toString());
            //System.out.println("Covered text is: "+tokens[names[si].getStart()] + " " + tokens[names[si].getStart()+1]);
            StringBuilder cb = new StringBuilder();
            for (int ti = names[si].getStart(); ti < names[si].getEnd(); ti++) {
                cb.append(tokens[ti]).append(" "); //<co id="co.opennlp.name.eachtoken"/>
            }



            String _name = cb.substring(0, cb.length() - 1);
            if (_name.matches("(.*)([0-9()!@#$%^&*]+)(.*)") ||
                    _name.matches("(.*)City(.*)") ||
                    _name.matches("(.*)city(.*)") ||
                    _name.matches("(.*)University(.*)") ||
                    _name.matches("(.*)university(.*)") ||
                    _name.matches("(.*)Areas(.*)") ||
                    _name.matches("(.*)areas(.*)") ||
                    _name.matches("(.*)E-mail(.*)") ||
                    _name.matches("(.*)e-mail(.*)")
                    ) {
                continue;
            }
            namesList.add(new Name(_name, names[si].getType(), spanProbs[si]));
            namesGet.add(_name);

            System.out.println(cb.substring(0, cb.length() - 1)); //<co id="co.opennlp.name.extra"/>
            System.out.println("\ttype: " + names[si].getType());
            System.out.println("Probability is: "+spanProbs[si]);

            out.println(_name);
        }

        out.flush();
        out.close();
        return namesGet;
    }

    public static void getTitle(String[] lines) {
        Pattern p0 = Pattern.compile("ISBN");
        Pattern p1 = Pattern.compile("ISSN");
        int test = 0;
        for (int i = 0; i < lines.length; ++i) {
            Matcher m0 = p0.matcher(lines[i]);
            Matcher m1 = p1.matcher(lines[i]);
            boolean b0 = m0.find();
            boolean b1 = m1.find();
            if (b0 || b1) {
                test++;
                continue;
            } else {
                System.out.print(lines[i]+"\n");
            }
            if (test >= 2) {
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String infile = args.length > 0 ? args[0] : "D:\\projects\\Java\\MetadataExtraction\\resources\\reports\\20.pdf";

        PDFToTXT pdftotxt = new PDFToTXT();
        try {
            pdftotxt.PDFtoTXT(infile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //read from file.txt
        //String fileStr = readLineByLineJava8("C:\\Users\\333da\\Desktop\\vkrreports\\out.txt");


        SentenceSplitter ss = new SentenceSplitter();
        //String[] lines = ss.split(fileStr);
        //String lines[] = fileStr.split("\n");

        //title
        System.out.print("TITLE\n");
        String[] lines = pdftotxt.getPage(0).split("\n");
        getTitle(lines);
        //names
        lines = ss.split(pdftotxt.getPages(0, 5));
        NameFinderME finder = new NameFinderME(
                new TokenNameFinderModel(new FileInputStream(new File("resources\\models\\en-person-custom-new.bin")))
                //new TokenNameFinderModel(new FileInputStream(new File("C:\\Users\\333da\\Desktop\\nlp\\en-ner-date.bin")))
        );

        InputStream modelInToken = new FileInputStream("resources\\models\\en-token.bin");
        TokenizerModel modelToken = new TokenizerModel(modelInToken);
        Tokenizer tokenizer = new TokenizerME(modelToken);

        Extractor extractor = new Extractor();

        List<String> namesGet = new ArrayList<String>();

        for (int i = 0; i < lines.length; ++i) {
            String[] tokens = tokenizer.tokenize(lines[i]);
            Span[] names = finder.find(tokens);
            double[] spanProbs = finder.probs(names);
            List<String> _namesGet = displayNames(names, spanProbs, tokens);
            namesGet.addAll(_namesGet);
            //extractor.Extract(lines[i]);
            //System.out.println("_____________");
        }
        finder.clearAdaptiveData();


        ///////////////////////

/*
        List<String> namesExpect = new ArrayList<String>();

        namesExpect.add("Yutaka Watanuki");
        namesExpect.add("Robert M. Suryan");
        namesExpect.add("Hiroko Sasaki");
        namesExpect.add("Takashi Yamamoto");
        namesExpect.add("Elliott L. Hazen");
        namesExpect.add("Martin Renner");
        namesExpect.add("Jarrod A. Santora");
        namesExpect.add("Patrick D. O’Hara");
        namesExpect.add("William J. Sydeman");

        System.out.println("PRECISION: " + precision(namesGet, namesExpect));
        System.out.println("RECALL: " + recall(namesGet, namesExpect));

        updateScores(namesGet, namesExpect);
        System.out.println("FM: " + getFMeasure());
*/
        ///////////////////////

        //System.out.println("Key words");
        //Process process = new ProcessBuilder()
        //        .command("resources\\scripts\\ner.py")
         //       .start();

        //System.out.println(process.waitFor());
        //Process p = Runtime.getRuntime().exec("python resources\\scripts\\ner.py");


        //sentence
        lines = ss.split(pdftotxt.getPages(0, pdftotxt.getSize()));
        for (int i = 0; i < lines.length; ++i) {
            extractor.Extract(lines[i]);
        }

    }

    private static String readLineByLineJava8(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
