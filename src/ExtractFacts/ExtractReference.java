package ExtractFacts;

import Training.NameDetectorTraining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractReference {
    //REGEX
    private String regexName = "([A-Za-z\\.])*[A-Z][A-Za-z]+,(\\s*)([A-Za-z\\.])*";
    private String regexNames = "(" + regexName + "(\\s?)[and,]*(\\s?)){1,}";
    private String regexEds = "(\\(Ed\\.\\))|(\\(Eds\\.\\))";

    private String regexNamesEds = regexNames + "(\\s*)" + "("+regexEds+")*";

    private String regexYear = "[0-9]*\\.";
    private String regexNamesEdsYear = regexNames + "(\\s*)" + "("+regexEds+")*" + "(\\s*)" + "("+regexYear+")*";

    //private String regexTitleAndCharacteristics = "[A-Za-z0-9/\\.,\\s();]*(\\s*)([0-9\\-:]+)(\\s*)((pp\\.))";
    private String regexTitleAndCharacteristics = "[A-Za-z0-9/\\.,\\s();]*[0-9]+(([\\-:][0-9]+\\.)|((\\s*)pp\\.))";

    private String regexReference = regexNamesEdsYear  + "(\\s*)" + regexTitleAndCharacteristics;

    private String sep = "(([\\-:][0-9]+\\.)|((\\s*)pp\\.))";

    private String text;
    public ExtractReference(String inputtext) {
        text = inputtext;
    }

    public void Extract() {
        Pattern p = Pattern.compile(regexReference);
        Matcher m = p.matcher(text);

        String refs = "";
        while(m.find()) {
            //System.out.println(m.start() + " " +text.substring(m.start(), m.end()));
            refs += text.substring(m.start(), m.end());
        }


        p = Pattern.compile(sep);
        m = p.matcher(refs);


        ArrayList<String> arrRefs = new ArrayList<>();

        int rangeControl = 0;
        while(m.find()) {
            arrRefs.add(refs.substring(rangeControl, m.end()));
            rangeControl = m.end();
            //System.out.println(m.start() + " " +refs.substring(m.start(), m.end()));
        }

        for(String elem : arrRefs){
            System.out.println(elem+"\n");
        }
    }

    public static void main(String[] args) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(
                    Paths.get("D:\\projects\\Java\\MetadataExtraction\\resources\\reports\\53.txt")));
                    //Paths.get("D:\\projects\\Java\\MetadataExtraction\\resources\\trainRef.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        new ExtractReference(content).Extract();

    }
}
