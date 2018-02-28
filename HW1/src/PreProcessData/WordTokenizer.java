package PreProcessData;

import java.util.StringTokenizer;

/**
 * This is for INFSCI 2140 in 2017 fall
 * 
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	//record the current word position
    private int position = 0;
    //record the number of words
    private int size;
    private final  String regx = "[^A-Za-z0-9]";
    private String[]words;


    /**
     * @constructor
     * @param texts
     */
	public WordTokenizer( char[] texts ) {
		// this constructor will tokenize the input texts (usually it is a char array for a whole document)
        String doc = String.valueOf(texts);
        String newdoc = doc.replaceAll(regx," ");
        this.words = newdoc.split("\\s+");
        this.size = words.length;

    }


    /**
     *This method is to iterator texts to get a next word
     * @return next word of a given document or null if reaching the end
     */
	// YOU MUST IMPLEMENT THIS METHOD
	public char[] nextWord() {
		// read and return the next word of the document
		// or return null if it is the end of the document
        char[] nextWord = null;
        if(this.position <= this.size - 1){
            nextWord = words[position].toCharArray();
            this.position++;
        }
		return nextWord;
	}
	
}
