package ie.adapt.tcd.nlp.text;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import esaz
import org.apache.lucene.analysis.TokenStream;

public class TextProcessor {
	WikipediaAnalyzer analyzer;
	TokenStream ts;
	public TextProcessor () throws IOException{
		analyzer = new WikipediaAnalyzer();
	}
	/**
	 * A method to break a document into sentences. 
	 * There are different libraries that could be used to split text into sentences like Stanford CorNLP
	 * @param document document's text
	 * @return ArrayList of sentences in the document if there are sentences, null otherwise 
	 */
	public ArrayList <String> sentenise (String document){
        BreakIterator boundary = BreakIterator.getSentenceInstance(Locale.US);
        ArrayList <String> sentences = new ArrayList <String> ();  
         boundary.setText(document);
         int start = boundary.first();
	     for (int end = boundary.next();
	          end != BreakIterator.DONE;
	          start = end, end = boundary.next()) {
	    	 sentences.add(document.substring(start,end));
	     }
	     sentences.removeAll(Arrays.asList("",null));// to remove empty sentences, just in case
	     if(sentences.size()==0)
	    	 return null;
	     
	     return sentences;
	} 
	
}
