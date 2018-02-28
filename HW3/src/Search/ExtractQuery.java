package Search;

import Classes.Path;
import Classes.Query;
import Classes.Stemmer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractQuery {
	private BufferedReader br;
    private BufferedReader stopWordReader;
    private Set<String> stopWords;
    private Pattern pattern;
    private Matcher matcher;
    private String cur;
	public ExtractQuery() {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming.
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
        stopWords = new HashSet<>();
        try{
            br = new BufferedReader(new InputStreamReader(new FileInputStream(Path.TopicDir)));
            stopWordReader = new BufferedReader(new InputStreamReader(new FileInputStream(Path.StopwordDir)));
            String line ="";
            while((line = stopWordReader.readLine())!=null){
                stopWords.add(line);
            }
            stopWordReader.close();

        }catch (IOException ex){
            ex.printStackTrace();
        }


	}
	
	public boolean hasNext() {
		try{
		    this.cur = br.readLine();
		    if(cur!=null){
              return true;
            }else{
		        br.close();
		        return false;
            }
        }catch (IOException ex){
		    ex.printStackTrace();
        }
	    return false;
	}
	
	public Query next() {
        pattern = Pattern.compile("^<title>(.*)");
        String topicID = "";
        StringBuilder sb = new StringBuilder();
        try {
            while (!cur.startsWith("</top>")) {
                cur = br.readLine();
                matcher = pattern.matcher(cur);
                if (cur.startsWith("<num>")) {
                    topicID = cur.split(":")[1].trim();
                }
                if (matcher.matches()) {
                    //queryContent = matcher.group(1);
                    sb.append(matcher.group(1)).append(" ");
                }
                // query can deal with description and narrative here
                
                /*if(cur.startsWith("<desc>")){
                    cur = br.readLine();
                    while(!cur.startsWith("<narr>")){
                        sb.append(cur).append(" ");
                        cur = br.readLine();
                    }
                }
                if(cur.startsWith("<narr>")){
                    cur = br.readLine();
                    while(!cur.startsWith("</top>")){
                        sb.append(cur).append(" ");
                        cur = br.readLine();
                    }
                }*/
            }
            String[]words = wordTokenizer(sb.toString());
            String newContent = normalizAndFilter(words);
            Query query = new Query();
            query.SetTopicId(topicID);
            query.SetQueryContent(newContent);
            return query;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String[]wordTokenizer(String content){
        String regx = "[^A-Za-z0-9]";
        String newContent = content.replaceAll(regx," ");
        String[]words = newContent.split("\\s+");
        return words;
    }

    public String normalizAndFilter(String[]words){
        StringBuilder sb = new StringBuilder();
        for(String word: words){
            String lowerStr = word.toLowerCase();
            if(!stopWords.contains(lowerStr)){
                char[] chars = lowerStr.toCharArray();
                Stemmer s = new Stemmer();
                s.add(chars,chars.length);
                s.stem();
                sb.append(s.toString()).append(" ");
            }
        }
        return  sb.toString();
    }

}
