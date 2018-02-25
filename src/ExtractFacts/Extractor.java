package ExtractFacts;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;

public class Extractor {
    private String modelStr = "C:\\Users\\333da\\Desktop\\nlp\\englishPCFG.ser.gz";

    public void Extractor() {}

    public void Extract(String sentence) throws FileNotFoundException {
        if (//sentence.contains("PICES") ||
                sentence.contains("Pacific") ||
                sentence.contains("pacific") ||
                sentence.contains("East") ||
                //sentence.contains("east") ||
                sentence.contains("West") ||
                //sentence.contains("west") ||
                sentence.contains("region") ||
                sentence.contains("Region") ||
                sentence.contains("Ocean") ||
                sentence.contains("ocean") ||
                sentence.contains("bay") ||
                sentence.contains("Bay")
                //sentence.contains("model") ||
                //sentence.contains("Model")
                ) {

        } else {
            return;
        }


        LexicalizedParser lexicalizedParser = LexicalizedParser.loadModel(modelStr);

        TokenizerFactory<CoreLabel> tokenizerFactory =
                PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        Tokenizer<CoreLabel> tokenizer =
                tokenizerFactory.getTokenizer(new StringReader(sentence));
        List<CoreLabel> wordList = tokenizer.tokenize();
        if (wordList.size() > 18) {
            return;
        }
        Tree paresTree = lexicalizedParser.apply(wordList);


        TreePrint treePrint = new TreePrint("typedDependenciesCollapsed");
        treePrint.printTree(paresTree);

        TreebankLanguagePack tlp = lexicalizedParser.treebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(paresTree);
        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();

        //System.out.println(tdl);
        PrintWriter out = new PrintWriter(new FileOutputStream("resources\\out\\trees.txt", true));

        for(TypedDependency dependency : tdl) {
            //System.out.println("Word: [" + dependency.gov()
            //        + "] Relation: [" + dependency.reln().getLongName()
            //        + "] Dependent Word: [" + dependency.dep() + "]");
            out.println("Word: [" + dependency.gov()
                    + "] Relation: [" + dependency.reln().getLongName()
                    + "] Dependent Word: [" + dependency.dep() + "]");
        }
        out.flush();
        out.close();
    }

}
