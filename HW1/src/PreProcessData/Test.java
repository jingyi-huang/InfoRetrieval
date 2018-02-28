package PreProcessData;

import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huang on 9/12/17.
 */
public class Test {



    public static void main(String[] args) throws Exception {

        Map<String, Object> doc = null;
        DocumentCollection corpus = new TrectextCollection();
        // process the corpus, document by document, iteractively
        int wordcount=0;
        int count = 0;
        while ((doc = corpus.nextDocument()) != null) {
            // load document number of the document
            String docno = doc.keySet().iterator().next();

            // load document content
            char[] content = (char[]) doc.get(docno);


            //initiate the WordTokenizer class
            WordTokenizer tokenizer = new WordTokenizer(content);

            // initiate a word object, which can hold a word
            char[] word = null;

            // process the document word by word iteratively
            while ((word = tokenizer.nextWord()) != null) {
                wordcount++;
            }
            count++;
            if(count%10000==0)
                System.out.println("finish "+count+" docs");
        }
        System.out.println("totaly document count:  "+count);
        System.out.println(" totally term count: "+wordcount);

    }
}
