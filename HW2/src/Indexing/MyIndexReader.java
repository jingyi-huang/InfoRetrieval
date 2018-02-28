package Indexing;

import Classes.Path;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class MyIndexReader {
	//Suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
    String dirPath;
    HashMap<String,Integer> docMap;
    HashMap<Integer,String>documents;
    BufferedReader postingReader;
    BufferedReader docReader;
    String[]posts;
    String searchedLine;
	
	public MyIndexReader( String type ) throws IOException {
		//read the index files you generated in task 1
		//remember to close them when you finish using them
		//use appropriate structure to store your index
        if(type.equals("trectext")){
            dirPath = Path.IndexTextDir;
        }else{
            dirPath = Path.IndexWebDir;
        }
        docReader = new BufferedReader(new InputStreamReader(new FileInputStream(dirPath+Path.docList)));
        postingReader = new BufferedReader(new InputStreamReader(new FileInputStream(dirPath+Path.postingList)));
        String line ="";
        docMap = new HashMap<>();
        documents = new HashMap<>();
        int i = 1;
        while((line=docReader.readLine())!=null){
            docMap.put(line,i);
            documents.put(i,line);
            i++;
        }
        docReader.close();

	}
	
	//get the non-negative integer dociId for the requested docNo
	//If the requested docno does not exist in the index, return -1
	public int GetDocid( String docno ) {
		if(docMap.containsKey(docno)){
		    return docMap.get(docno);
        }
	    return -1;

	}

	// Retrieve the docno for the integer docid
	public String GetDocno( int docid ) {
        return documents.get(docid);
	}
	
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 *
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] GetPostingList( String token ) throws IOException {
	    int[][] postingList;

        if(this.posts!=null){
	        postingList = new int[this.posts.length][2];
            int i = 0;
            for(String post: posts){
                String[]details = post.split(" ");
                postingList[i][0]=Integer.parseInt(details[0])+1;
                postingList[i][1]= Integer.parseInt(details[1]);
                i++;
            }
            return postingList;
        }
	    return null;
	}

	// Return the number of documents that contains the token.
	public int GetDocFreq( String token ) throws IOException {
	    String line ="";
        while((line=postingReader.readLine())!=null){
            if(line.equalsIgnoreCase(token)){
                searchedLine = postingReader.readLine();
                posts = searchedLine.split(";");
                return posts.length;
            }
        }

        return 0;
	}
	
	// Return the total number of times the token appears in the collection.
	public long GetCollectionFreq( String token ) throws IOException {
        long sum = 0;
        if(this.posts!=null){
            for(String post: posts){
                String[]details = post.split(" ");
                sum +=Long.parseLong(details[1]);
            }
        }
        return sum;
	}
	
	public void Close() throws IOException {
	    postingReader.close();
	}
	
}