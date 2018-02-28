package PreProcessData;
import Classes.Path;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class StopWordRemover {
	//you can add essential private methods or variables in 2017.
    private FileInputStream input;
    private BufferedReader br;
    private Set<String> stopWords;

    /**
     * @constructor
     * read the stopword file and load it into a set
     */
	public StopWordRemover( ) {
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.StopwordDir
        this.stopWords = new HashSet<>();
        String line="";
        try{
            this.input = new FileInputStream(Path.StopwordDir);
            this.br = new BufferedReader(new InputStreamReader(input));
        }catch(IOException e){
            e.printStackTrace();
        }
        try{
            while((line = br.readLine())!= null){
                if(!line.isEmpty()){
                    stopWords.add(line);
                }
            }
            br.close();
        }catch(IOException e){
            e.printStackTrace();
        }

	}

    /**
     * check if a word is a stopword or not
     * @param word
     * @return boolean
     */
	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword( char[] word ) {
		// return true if the input word is a stopword, or false if not

		return stopWords.contains(new String(word));
	}
}
