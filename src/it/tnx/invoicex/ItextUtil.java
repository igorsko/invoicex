/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.tnx.invoicex;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author test1
 */
public class ItextUtil {

    public static void concatenate(String outFile, String[] args) {
        try {
            int pageOffset = 0;
            ArrayList master = new ArrayList();
            int f = 0;
            Document document = null;
            PdfCopy writer = null;
            while (f < args.length) {
                // we create a reader for a certain document
                PdfReader reader = new PdfReader(args[f]);
                reader.consolidateNamedDestinations();
                // we retrieve the total number of pages
                int n = reader.getNumberOfPages();
                List bookmarks = SimpleBookmark.getBookmark(reader);
                if (bookmarks != null) {
                    if (pageOffset != 0) {
                        SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
                    }
                    master.addAll(bookmarks);
                }
                pageOffset += n;
                System.out.println("There are " + n + " pages in " + args[f]);

                if (f == 0) {
                    // step 1: creation of a document-object
                    document = new Document(reader.getPageSizeWithRotation(1));
                    // step 2: we create a writer that listens to the document
                    writer = new PdfCopy(document, new FileOutputStream(outFile));
                    // step 3: we open the document
                    document.open();
                }
                // step 4: we add content
                PdfImportedPage page;
                for (int i = 0; i < n;) {
                    ++i;
                    page = writer.getImportedPage(reader, i);
                    writer.addPage(page);
                    System.out.println("Processed page " + i);
                }
                writer.freeReader(reader);
                f++;
            }
            if (!master.isEmpty()) {
                writer.setOutlines(master);
                // step 5: we close the document
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}