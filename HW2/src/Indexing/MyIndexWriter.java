package Indexing;

import Classes.Path;
import java.io.*;
import java.util.*;

public class MyIndexWriter {
    // Suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...

    //String->term, Integer->termId
    Map<String, Integer> terms;
    //list index is the docId of documents
    List<String> documents;
    //postingList
    List<Map<Integer,Integer>> dict;
    private int counter;
    int termId;
    int blockNumber;
    final int LIMIT = 10000;
    private String dirPath;

    /**
     * This constructor initialize the path files are written to
     * @param type String, type of file we are gonna indexing
     * @throws IOException
     */
	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
         this.termId = 0;
         this.blockNumber = 0;
         if(type.equals("trectext")){
             dirPath = Path.IndexTextDir;
         }else{
             dirPath = Path.IndexWebDir;
         }
         new File(dirPath).mkdir();

         terms = new LinkedHashMap<String,Integer>();
         documents = new ArrayList<String>();
         dict = new ArrayList<Map<Integer,Integer>>();
	}

    /**
     * This method is to index a document token by token. When the number of processed documents exceed the limit, write into blocks.
     * @param docno  String, document number
     * @param content String, document content
     * @throws IOException
     */
	public void IndexADocument(String docno, String content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader

        //update documents
        documents.add(docno);
        //assign integer docId from 0
        int docId = documents.size()-1;
        Map<Integer,Integer>postingList;

		String[] words = content.split("\\s+");

		for(String word: words){
		    //if this is a new word
		    if(!this.terms.containsKey(word)){
		        //update term records
                terms.put(word,termId);
                postingList = new LinkedHashMap<Integer,Integer>();
                //update posting list
                postingList.put(docId,1);
                dict.add(termId,postingList);
                termId++;
            //if the term already exists
            }else{
                int tokenId = this.terms.get(word);
                //if the posting list of the term has not been written to block
                if(dict.get(tokenId)!=null){
                    postingList = dict.get(tokenId);

                    //check if this is a new doc
                    if(postingList.containsKey(docId)){
                        postingList.put(docId,postingList.get(docId)+1);
                    }else{
                        postingList.put(docId,1);
                    }
                    dict.set(tokenId,postingList);

                    //if the posting list of the term has been written to block
                }else{
                    postingList = new LinkedHashMap<Integer,Integer>();
                    postingList.put(docId,1);
                    dict.set(tokenId,postingList);
                }
            }
        }
        ++counter;
		if(counter % LIMIT == 0){
		    writeBlock();
        }
	}

    /**
     * This method is to write posting list in the memory into block.
     * @throws IOException
     */
	public void writeBlock() throws IOException{

        FileWriter blockFile = new FileWriter(dirPath+Path.IndexBlock+"_"+blockNumber);
        BufferedWriter out = new BufferedWriter(blockFile);
	    for(int i = 0;i< dict.size();i++){
	        Map<Integer,Integer> items = dict.get(i);
	        if(items == null){
                //out.write("no this term in this block");
                out.append("\n");
            }else{
                Iterator<Integer> itr = items.keySet().iterator();
                while(itr.hasNext()){
                    Integer docId = itr.next();
                    Integer frequency = items.get(docId);
                    out.write(docId+" "+frequency+";");
                }
                out.append("\n");
            }

        }
        out.flush();
        out.close();
        //clear all written posting lists
        for(int i =0; i<dict.size();i++){
            dict.set(i,null);
        }
        blockNumber++;
    }

    /**
     * This method is to merge all blocks into one file --postingList
     * @throws IOException
     */
    public void mergeBlocks() throws IOException{
        FileWriter fw = new FileWriter(dirPath+Path.postingList);
        FileInputStream []streams = new FileInputStream[blockNumber];
        BufferedReader []readers = new BufferedReader[blockNumber];
        String[]lines = new String[blockNumber];

        for(int i=0; i<blockNumber;i++) {
            streams[i] = new FileInputStream(dirPath + Path.IndexBlock + "_" + i);
            readers[i] = new BufferedReader(new InputStreamReader(streams[i]));
            lines[i] = readers[i].readLine();
        }
        Iterator<String> termIterator = terms.keySet().iterator();
        while(lines[blockNumber-1]!=null){
            StringBuilder sb = new StringBuilder();
            sb.append(termIterator.next()+"\n");
            for(int j=0;j<lines.length;j++) {
                if(lines[j] != null){
                    sb.append(lines[j]);
                    lines[j]=readers[j].readLine();
                }
            }
            sb.append("\n");
            fw.write(sb.toString());
        }
        fw.flush();
        fw.close();
        for(int i=0; i<blockNumber; i++){
            readers[i].close();
            streams[i].close();
        }
    }

    /**
     * This method is to write document number one by one into a file
     * @throws IOException
     */
    public void writeDocs() throws IOException{
	    FileWriter fw = new FileWriter(dirPath+Path.docList);
	    for(int i=0; i<documents.size();i++){
	        fw.write(documents.get(i)+"\n");
        }
        fw.close();
    }

    /**
     * This method is implemented at the final stage of write indexing.
     * It writes remaining stuff in memory, merges all blocks and writes document number file.
     * @throws IOException
     */
	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
		// write remaning blocks and merge
         writeBlock();
         mergeBlocks();
         writeDocs();

	}
	
}
