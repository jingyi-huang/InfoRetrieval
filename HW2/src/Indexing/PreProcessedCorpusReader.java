package Indexing;

import Classes.Path;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreProcessedCorpusReader {

    private BufferedReader br;
    private FileInputStream input;
    private int length;

    /**
     * This constructor open the preprocess corpus file
     * @param type String, indicates the file type we are about to read
     * @throws IOException
     */
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, OR download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp.
		// Close the file when you do not use it any more
		if(type.equalsIgnoreCase("trecweb")){
            this.input = new FileInputStream(Path.DataWebDir);
        }else{
            this.input = new FileInputStream(Path.DataTextDir);
        }
         this.br = new BufferedReader(new InputStreamReader(input));
		this.length = 0;
	}

    /**
     * This method is to put doc number and its content into a map
     * @return document in Map<String, String> format
     * @throws IOException
     */
	public Map<String, String> NextDocument() throws IOException {
		// read a line for docNo, put into the map with <"DOCNO", docNo>
		// read another line for the content , put into the map with <"CONTENT", content>
       String docNo;
       String content;

        Map<String,String> document = new HashMap<>();
        docNo = br.readLine();
        String[]tokens;
        if(docNo != null){
            content = br.readLine();
            //tokens = content.split(" ");
            document.put("DOCNO",docNo);
            document.put("CONTENT",content);
            //length = length+tokens.length;
            return document;
        }

        br.close();
		return null;
	}

	public int collectLength(){
	    return this.length;
    }

}
