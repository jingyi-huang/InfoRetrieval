package PseudoRFSearch;

import java.util.*;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import SearchLucene.QueryRetrievalModel;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	private final double miu = 2000;
    private final int collectionLength = 142065539;


	public PseudoRFRetrievalModel(MyIndexReader ixreader)
	{
		this.ixreader=ixreader;
	}
	
	/**
	 * Search for the topic with pseudo relevance feedback in 2017 fall assignment 4.
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {	
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')
		
		
		//get P(token|feedback documents)
		HashMap<String,Double> TokenRFScore=GetTokenRFScore(aQuery,TopK);

        String[] tokens = aQuery.GetQueryContent().split(" ");
        //store the token does not exist in the whole collection
        Set<String> unseenWords = new HashSet<>();
        //store the documents in which token appears
        Set<Integer>relevantDocs = new HashSet<>();
        //List<Map<Integer,Integer>> tokenAndDocs = new ArrayList<>();
        Map<String,Map<Integer,Integer>> tokenAndDocs = new HashMap<>();

        PriorityQueue<Document> documents = new PriorityQueue<>(TopN, (d1, d2) -> {
            if(d1.score()>d2.score()){
                return -1;
            }else if(d1.score() < d2. score()){
                return 1;
            }else{
                return 0;
            }
        });

        for(String token: tokens){
            int[][]postingList = ixreader.getPostingList(token);
            HashMap<Integer, Integer> tokenDetails = new HashMap<>();
            //check if the token exists in the collection
            if(postingList == null){
                unseenWords.add(token);
            }else{
                for(int i =0; i<postingList.length;i++){
                    //assign doc id, doc frequency
                    tokenDetails.put(postingList[i][0], postingList[i][1]);
                    relevantDocs.add(postingList[i][0]);
                }
            }
            tokenAndDocs.put(token,tokenDetails);
        }
        //do Dirichlet smoothing
        for(int docId: relevantDocs){
            String docNo = ixreader.getDocno(docId);
            int docLength = ixreader.docLength(docId);
            double score = 1.0;

            for(String term: tokens){
                long collectionCount;
                if(unseenWords.contains(term)){
                    // if the token does not exist in the whole collection, we assign 1
                    collectionCount = 1;
                }else{
                    collectionCount = ixreader.CollectionFreq(term);
                }
                Map<Integer,Integer> tokenRecords = tokenAndDocs.get(term);
                int docFreq = tokenRecords.containsKey(docId)? tokenRecords.get(docId):0;
                double prob = (docFreq + miu * collectionCount/collectionLength)/(docLength + miu);
                double revisedProb = alpha*prob+(1-alpha)*TokenRFScore.get(term);
                score *= revisedProb;
            }

            Document doc = new Document(Integer.toString(docId),docNo,score);
            documents.add(doc);
        }

        // sort all retrieved documents from most relevant to least, and return TopN
        List<Document> results = new ArrayList<Document>();
        for(int i = 0; i<TopN; i++){
            results.add(documents.poll());
        }
		return results;
	}
	
	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception
	{
		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score> in HashMap TokenRFScore, and return it
        HashMap<String,Double> TokenRFScore=new HashMap<String,Double>();
        QueryRetrievalModel retrievalModel = new QueryRetrievalModel(ixreader);
        Set<Integer> topDocsId = new HashSet<>();
        List<Document> topDocuments = retrievalModel.retrieveQuery(aQuery,TopK);
        int DocsLength = 0;
        for(Document doc: topDocuments){
            int docID = Integer.parseInt(doc.docid());
            topDocsId.add(docID);
            int docLength = ixreader.docLength(docID);
            DocsLength +=docLength;
        }

        String[]tokens = aQuery.GetQueryContent().split(" ");

        //calculate the score for each token using Dirichlet smoothing
        for(String token: tokens){
            int[][] postingList = ixreader.getPostingList(token);
            long collectionFreq;
            int docFreq = 0;
            if(postingList == null){
                // if the token does not exist in the whole collection, we assign 1
                collectionFreq = 1;
            }else{
                collectionFreq = ixreader.CollectionFreq(token);
                for(int i = 0; i<postingList.length; i++){
                    if(topDocsId.contains(postingList[i][0])){
                        docFreq +=postingList[i][1];
                    }
                }
            }
            double prob = (docFreq + miu * collectionFreq/collectionLength)/(DocsLength + miu);
            TokenRFScore.put(token,prob);
        }

		return TokenRFScore;
	}

}