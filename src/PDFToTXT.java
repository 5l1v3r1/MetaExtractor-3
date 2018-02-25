import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PDFToTXT {
    public PDFToTXT() {
        pages = new ArrayList<String>();
    }

    private ArrayList<String> pages;

    public void PDFtoTXT(String pdfFile) throws IOException {
        /*try {
            PrintWriter out = new PrintWriter(new FileOutputStream("resources\\scripts\\out.txt"));

            File file = new File(pdfFile);
            PDDocument pdDocument = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();

            int count = pdDocument.getNumberOfPages();
            for (int i = 1; i <= count; ++i) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String text = stripper.getText(pdDocument);
                pages.add(text);
                out.println(text);
            }
            pdDocument.close();
            out.flush();
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }*/

        PdfReader reader = new PdfReader(pdfFile);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        PrintWriter out = new PrintWriter(new FileOutputStream("resources\\scripts\\out.txt"));
        //PrintWriter out = new PrintWriter(new FileOutputStream("C:\\Users\\333da\\Desktop\\train\\9.txt"));
        TextExtractionStrategy strategy;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            strategy = parser.processContent(i, new LocationTextExtractionStrategy());
            pages.add(strategy.getResultantText());
            out.println(strategy.getResultantText());
        }
        reader.close();
        out.flush();
        out.close();
    }

    public String getPages(int start, int end) {
        String res = "";

        for (int i = start; i < end; ++i) {

            if (pages.get(i).contains("pp.")
                    ) {
                continue;
            }

            res += pages.get(i);
        }

        return res;
    }

    public String getPage(int count) {
        return pages.get(count);
    }

    public int getSize() {
        return pages.size();
    }
}
