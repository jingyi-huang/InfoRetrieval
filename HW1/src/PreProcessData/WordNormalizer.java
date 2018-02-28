package PreProcessData;
import Classes.Stemmer;

/**
 * This is for INFSCI 2140 in 2017
 * 
 */
public class WordNormalizer {


    /**
     * @param chars
     * @return lowercase chars
     */
	// YOU MUST IMPLEMENT THIS METHOD
	public char[] lowercase( char[] chars ) {
		//transform the uppercase characters in the word to lowercase
		for(int i = 0; i<chars.length;i++){
           chars[i] = Character.toLowerCase(chars[i]);
        }
		return chars;
	}

    /**
     * This method is to stem on input word
     * @param chars
     * @return stemmed word
     */
	public String stem(char[] chars)
	{
		//use the stemmer in Classes package to do the stemming on input word, and return the stemmed word
        Stemmer s = new Stemmer();
        s.add(chars, chars.length);
        s.stem();
		return s.toString();
	}
	
}
