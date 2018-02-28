package Search;

import java.io.IOException;
import java.util.*;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
	private final double miu = 2000;
	//the collection length is estimated via HW2.PreprocessdCorpusReader
    //calculation based on the length of content of each document
	private final int collectionLength = 142065539;
    //private final int collectionLength = 289080751;

	public QueryRetrievalModel(MyIndexReader ixreader) {

		indexReader = ixreader;
	}
	
	/**
	 * Search for the topic information. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */
	
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low
        String[] strs = aQuery.GetQueryContent().split(" ");
        List<String> tokens = new ArrayList<String>();
        for(int i =1; i<strs.length; i++){
            tokens.add(strs[i]);
        }
        //store the documents in which token appears
        Set<Integer>relevantDocs = new HashSet<>();
        //store the token does not exist in the whole collection
        Set<String> unseenWords = new HashSet<>();
        List<Map<Integer,Integer>> tokenAndDocs = new ArrayList<>();

        PriorityQueue<Document> documents = new PriorityQueue<>(TopN,new Comparator<Document>(){
           @Override
            public int compare(Document d1, Document d2){
               if(d1.score()>d2.score()){
                   return -1;
               }else if(d1.score() < d2. score()){
                   return 1;
               }else{
                   return 0;
               }
           }
        });

        for(String token: tokens) {
            int[][] postingList = indexReader.getPostingList(token);
            HashMap<Integer, Integer> tokenDetails = new HashMap<>();
            //check if the token exists in the collection
            if (postingList == null) {
                unseenWords.add(token);
            }else{
                for (int i = 0; i < postingList.length; i++) {
                    //assign doc id, doc frequency
                    tokenDetails.put(postingList[i][0], postingList[i][1]);
                    relevantDocs.add(postingList[i][0]);
                }
            }
            tokenAndDocs.add(tokenDetails);
        }
            //do Dirichlet smoothing
            for(Integer docId: relevantDocs){
                String docNo = indexReader.getDocno(docId);
                int docLength = indexReader.docLength(docId);
                double score = 1.0;

                for(String word: tokens){
                    long collectionCount;
                    if(unseenWords.contains(word)){
                        // if the token does not exist in the whole collection, we assign 1
                        collectionCount= 1;
                    }else{
                        collectionCount = indexReader.CollectionFreq(word);
                    }
                    int position = tokens.indexOf(word);
                    Map<Integer,Integer> tokenRecords = tokenAndDocs.get(position);
                    int docFreq = tokenRecords.containsKey(docId)? tokenRecords.get(docId):0;
                    double prob = (docFreq + miu * collectionCount/collectionLength)/(docLength + miu);
                    score = score * prob;
                }

                //add post-calculated document to a maxheap
                Document doc = new Document(Integer.toString(docId),docNo,score);
                documents.add(doc);
            }
            //retrieve top k documents
            List<Document> results = new ArrayList<>();
            for(int i = 0; i<TopN; i++){
                results.add(documents.poll());
            }

		return results;
	}
	
}