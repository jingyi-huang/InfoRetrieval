package PreProcessData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Classes.Path;

/**
 * This is for INFSCI 2140 in 2017
 *
 */
public class TrectextCollection implements DocumentCollection {

	private BufferedReader br;
	private FileInputStream input;
	private Pattern pattern;
	private Matcher matcher;




	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrectextCollection() throws IOException {
        // This constructor should open the file in Path.DataTextDir
        // and also should make preparation for function nextDocument()
        // you cannot load the whole corpus into memory here!!
	    this.input = new FileInputStream(Path.DataTextDir);
        this.br = new BufferedReader(new InputStreamReader(input));
        String regex = "<DOCNO>(.*)</DOCNO>";
        this.pattern = Pattern.compile(regex);


	}

    /**
     * @return  Map<String,Object> next document in the file or null reaching the end of the file
     * doc number as its key and doc content as its value
     * @throws IOException
     */
	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NTT: remember to close the file that you opened, when you do not use it any more
        String docNo ="";
        String line ="";

        while((line = this.br.readLine())!= null) {
            this.matcher = this.pattern.matcher(line);
            if (matcher.matches()) {
                Map<String,Object> document = new HashMap<>();
                docNo = matcher.group(1);
                while (!line.equals("<TEXT>")) {
                    line = br.readLine();
                }
                StringBuilder builder = new StringBuilder();
                line = br.readLine();
                while (!line.equals("</TEXT>")) {
                    if(!line.isEmpty()){
                        builder.append(line).append("\n");
                    }

                    line = br.readLine();
                }
                document.put(docNo, builder.toString().toCharArray());
                return document;
            }

        }
        br.close();
        return null;
	}
	
}
